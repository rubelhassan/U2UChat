package com.example.rubel.u2uchat.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.Util.AppConstants;
import com.example.rubel.u2uchat.adapter.SearchListAdapter;
import com.example.rubel.u2uchat.app.UserProfileActivity;
import com.example.rubel.u2uchat.model.User;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rubel on 4/14/2017.
 */

public class SearchUserFragment extends Fragment {

    SearchListAdapter mSearchListAdapter;
    List<User> mSearchedUsers;
    RecyclerView mRecyclerView;
    private boolean mShouldProgress;
    private ProgressBar mProgressBar;
    //Firebase API Clients
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser mCurrentAppUser;
    private GeoFire mGeoFireRef;
    private GeoQuery mGeoQuery;
    private GeoLocation mAppLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchedUsers = new ArrayList<>();
        mSearchListAdapter = new SearchListAdapter(mSearchedUsers, getContext());
        mShouldProgress = true;

        setCurrentAppUser();

        setAdapterListener();

        initAppLocation();

        initFirebaseDatabaseAndStorage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);

        showProgress(view);

        setRecyclerViewAdapter(view);

        findNearestUser();

        return view;
    }

    private void initAppLocation() {
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(AppConstants.APP_PREFERENCE, MODE_PRIVATE);

        String locationString = sharedPreferences.getString(AppConstants.APP_LOCATION, "No");

        if (locationString.equals("No"))
            mAppLocation = new GeoLocation(23.810332, 90.412518);
        else
            mAppLocation = new Gson().fromJson(locationString, GeoLocation.class);

        Log.i("SearchLoc:", mAppLocation.toString());
    }



    private void setCurrentAppUser() {
        mCurrentAppUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("USER:", mCurrentAppUser.getUid());
        if (mCurrentAppUser == null) {
            // TODO
        }
    }

    private void setAdapterListener() {
        mSearchListAdapter.setSearchItemClickListener(new SearchListAdapter.SearchItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                User user = mSearchListAdapter.getItem(position);
                Toast.makeText(getContext(), user.getFullName(),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra(AppConstants.PROFILE_USER, user);
                startActivity(intent);
            }
        });
    }

    private void findNearestUser() {
        // TODO search based on user preferences
        mGeoQuery = mGeoFireRef.queryAtLocation(mAppLocation, 10);
        attachGeoQueryListener();
    }

    private void attachGeoQueryListener() {
        mGeoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                String[] slimUser = key.split(";");

                mFirebaseDatabaseReference.child(slimUser[0])
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    addResultToList(dataSnapshot);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                hideProgress();
                if (mSearchedUsers.isEmpty())
                    Snackbar.make(mRecyclerView, "No friends found.", Snackbar.LENGTH_INDEFINITE).show();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void addResultToList(DataSnapshot dataSnapshot) {
        hideProgress();

        User loadedUser = new User(
                dataSnapshot.child("userName").getValue().toString(),
                dataSnapshot.child("email").getValue().toString(),
                dataSnapshot.child("fullName").getValue().toString(),
                dataSnapshot.child("uid").getValue().toString(),
                dataSnapshot.child("photoUrl").getValue().toString(),
                dataSnapshot.child("isOnline").getValue().toString().equals("true"));

        if (!loadedUser.getUid().equals(mCurrentAppUser.getUid())) {
            mSearchedUsers.add(loadedUser);
            mSearchListAdapter.notifyDataSetChanged();
        }
    }

    private void showProgress(View view) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_fragment_search_user);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (mShouldProgress) {
            mShouldProgress = false;
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void initFirebaseDatabaseAndStorage() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mGeoFireRef = new GeoFire(mFirebaseDatabase.getReference().child("geofire"));
    }

    public void setRecyclerViewAdapter(View view) {
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView = (RecyclerView) view.findViewById(
                R.id.recycler_view_fragment_search_user);
        mRecyclerView.setAdapter(mSearchListAdapter);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        mSearchedUsers.clear();
        mSearchListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
