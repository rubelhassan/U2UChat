package com.example.rubel.u2uchat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.adapter.FriendsAdapter;
import com.example.rubel.u2uchat.model.Friend;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubel on 4/14/2017.
 */

public class SearchUserFragment extends Fragment {

    FriendsAdapter mFriendsAdapter;
    List<Friend> mFriendsList;

    private boolean mShouldProgress;
    private ProgressBar mProgressBar;

    //Firebase API Clients
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendsList = new ArrayList<>();
        mFriendsAdapter = new FriendsAdapter(mFriendsList, getContext());
        mShouldProgress = true;
        initFirebaseDatabaseAndStorage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);

        showProgress(view);

        setRecyclerViewAdapter(view);

        attachDatabaseListener();

        return view;
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
    }

    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    hideProgress();

                    Friend friend = new Friend(
                            dataSnapshot.child("userName").getValue().toString(),
                            dataSnapshot.child("photoUrl").getValue().toString(),
                            dataSnapshot.child("isOnline").getValue().toString().equals("true"));

                    mFriendsList.add(friend);
                    mFriendsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        mFirebaseDatabaseReference.addChildEventListener(mChildEventListener);
    }

    public void setRecyclerViewAdapter(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_fragment_search_user);
        recyclerView.setAdapter(mFriendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        mFriendsList.clear();
        mFriendsAdapter.notifyDataSetChanged();
    }
}
