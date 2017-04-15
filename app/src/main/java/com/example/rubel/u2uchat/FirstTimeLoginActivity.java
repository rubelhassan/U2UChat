package com.example.rubel.u2uchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.rubel.u2uchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirstTimeLoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static String USER_THUMB_URL = "https://firebasestorage.googleapis.com/v0/b/u2u-chat.appspot.com/o/default_user_pic.png?alt=media&token=fe979015-b0f4-4f18-96f3-9163d0b78574";
    FirebaseAuth mAuth;
    private EditText mEditTextUserName;
    private EditText mEditTextFullName;
    private Button btnEnter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserNamesDatabase;
    private DatabaseReference mUsersDatabase;
    private ChildEventListener mUsersChildListener;
    private boolean mUserExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        bindViews();

        mAuth = FirebaseAuth.getInstance();

        verifyCurrentUserLogin();

        readyFirebaseRealtimeClients();

        btnEnter.setOnClickListener(this);

    }

    private void verifyCurrentUserLogin() {
        mFirebaseUser = mAuth.getCurrentUser();

        if(mFirebaseUser == null){
            startActivity(new Intent(FirstTimeLoginActivity.this, SignInActivity.class));
            finish();
        }
    }

    private void bindViews() {
        mEditTextFullName = (EditText) findViewById(R.id.edit_text_fullname_new);
        mEditTextUserName = (EditText) findViewById(R.id.edit_text_username_new);
        btnEnter = (Button) findViewById(R.id.btn_go_new);
    }

    private void readyFirebaseRealtimeClients(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference();
        mUsersDatabase = mFirebaseDatabaseReference.child("users/" + mFirebaseUser.getUid() );
        mUserNamesDatabase = mFirebaseDatabaseReference.child("user_names");
        addDatabaseListener();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mUsersDatabase.removeEventListener(mUsersChildListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUsersDatabase.addChildEventListener(mUsersChildListener);
    }


    boolean validateUserInputs(String userName, String fullName){
        if(TextUtils.isEmpty(userName) || userName.length() < 3){
            mEditTextUserName.setError("minimum 3 characters");
        }

        if(TextUtils.isEmpty(fullName)
                || TextUtils.split(fullName, " ").length < 2 || fullName.length() < 5){
            mEditTextFullName.setError("enter full name");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_go_new:{
                String userName = mEditTextUserName.getText().toString().trim();
                String fullName = mEditTextFullName.getText().toString().trim();

                if(!validateUserInputs(userName, fullName)) break;

                User user = new User(userName, mFirebaseUser.getEmail(), fullName,
                        mFirebaseUser.getUid(), USER_THUMB_URL, true);

                saveUserIfNotExist(user);

                break;
            }default: break;
        }
    }

    private void saveUserIfNotExist(final User user) {
        mUserNamesDatabase.child(user.getUserName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mEditTextUserName.setError("username exists!");
                    Log.d("USER:", "user exist");
                }else{
                    mUsersDatabase.setValue(user);
                    mUserNamesDatabase.child(user.getUserName()).setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addDatabaseListener(){
        mUsersChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // user profile set successful
                if(dataSnapshot != null) {
                    startActivity(new Intent(FirstTimeLoginActivity.this, MainActivity.class));
                    finish();
                }
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

        mUsersDatabase.addChildEventListener(mUsersChildListener);
    }
}
