package com.example.rubel.u2uchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rubel.u2uchat.Util.AppUtils;
import com.example.rubel.u2uchat.adapter.FriendsAdapter;
import com.example.rubel.u2uchat.model.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PHOTO_PICKER = 101;
    private static final int REQUEST_SIGN_IN = 102;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 103;
    private static final String ANONYMOUS = "anonymous";

    // UI elements
    RecyclerView mRecyclerViewFriends;
    FriendsAdapter mAdapterFriends;
    List<Friend> mListFriends;
    ProgressBar mProgressBar;

    //Firebase API Clients
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;
    private FirebaseUser mFirebaseUser;

    private String mUser;
    private boolean mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!AppUtils.isConnectedToIntenet(this)) {
            Toast.makeText(this, "No intenet connection!", Toast.LENGTH_LONG).show();
            finish();
        }

        initViews();

        mProgress = true;
        mUser = ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        verifyLoginUser();

        initFirebaseDatabaseAndStorage();

        checkFirstTimeLogin();

        initializeAndSetAuthStateListener();

        attachDatabaseListener();

        makeUserOnline();
    }

    private void initFirebaseDatabaseAndStorage() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("users");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotoStorageReference = mFirebaseStorage.getReference().child("user_photos");
    }

    private void checkFirstTimeLogin() {
        DatabaseReference child = mFirebaseDatabaseReference.child(mFirebaseUser.getUid() + "/");

        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    startActivity(new Intent(MainActivity.this, FirstTimeLoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void verifyLoginUser() {
        if (mFirebaseUser == null) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
    }

    private void initViews() {
        mListFriends = new ArrayList<>();
        mRecyclerViewFriends = (RecyclerView) findViewById(R.id.recycler_view_friends_main);
        mAdapterFriends = new FriendsAdapter(mListFriends, this);
        mRecyclerViewFriends.setAdapter(mAdapterFriends);
        mRecyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_main);
        mProgressBar.setVisibility(View.VISIBLE);
    }


    private void initializeAndSetAuthStateListener() {
        mFirebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser == null) {
                    onSignedOutClenup();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                } else {
                    onSignedInInit(mFirebaseUser.getDisplayName());
                }
            }
        };

        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener);

    }


    private void onSignedInInit(String displayName) {
        mUser = displayName;
    }

    private void onSignedOutClenup() {
        mUser = ANONYMOUS;
        mListFriends.clear();
        mAdapterFriends.notifyDataSetChanged();
        detachDatabaseListener();
    }

    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (mProgress) {
                        mProgress = false;
                        mProgressBar.setVisibility(View.GONE);
                    }
                    Friend friend = new Friend(
                            dataSnapshot.child("userName").getValue().toString(),
                            dataSnapshot.child("photoUrl").getValue().toString(),
                            dataSnapshot.child("isOnline").getValue().toString().equals("true"));
                    mListFriends.add(friend);
                    mAdapterFriends.notifyDataSetChanged();
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

    private void detachDatabaseListener() {
        if (mChildEventListener != null)
            mFirebaseDatabaseReference.removeEventListener(mChildEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFirebaseAuth != null)
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthStateListener);
        mListFriends.clear();
        mAdapterFriends.notifyDataSetChanged();
        detachDatabaseListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out_main) {
            makeUserOffline();
            mFirebaseAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeUserOffline() {
        mFirebaseDatabaseReference.child(mFirebaseUser.getUid()).child("isOnline").setValue(false);
    }

    private void makeUserOnline() {
        mFirebaseDatabaseReference.child(mFirebaseUser.getUid()).child("isOnline").setValue(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
