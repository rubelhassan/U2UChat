package com.example.rubel.u2uchat.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FirstTimeLoginActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int REQUEST_PICK_PHOTO_GALLARY = 901;
    private static final int REQUEST_PICK_PHOTO_CAMERA = 902;
    private static String USER_THUMB_URL = "https://firebasestorage.googleapis.com/v0/b/u2u-chat.appspot.com/o/default_user_pic.png?alt=media&token=fe979015-b0f4-4f18-96f3-9163d0b78574";
    Bitmap bitmap;
    // UI components
    private EditText mEditTextUserName;
    private EditText mEditTextFullName;
    private Button btnEnter;
    private EditText mEditTextAge;
    private RadioGroup mRadioGroupSex;
    private RadioButton mRadioButtonSex;
    private ImageButton mImageButtonGallary;
    // Firebase clients
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserNamesDatabase;
    private DatabaseReference mUsersDatabase;
    private ChildEventListener mUsersChildListener;
    private boolean mUserExists;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_login);

        bindViews();

        mImageUri = null;

        mAuth = FirebaseAuth.getInstance();

        verifyCurrentUserLogin();

        readyFirebaseRealtimeClients();

        setViewListeners();

    }

    private void setViewListeners() {
        btnEnter.setOnClickListener(this);
        mImageButtonGallary.setOnClickListener(this);
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
        mEditTextAge = (EditText) findViewById(R.id.edit_text_email_age_new);
        mRadioGroupSex = (RadioGroup) findViewById(R.id.radio_group_sex);
        mImageButtonGallary = (ImageButton) findViewById(R.id.image_button_gallary);
        btnEnter = (Button) findViewById(R.id.btn_go_new);
    }

    private void readyFirebaseRealtimeClients(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference();
        mUsersDatabase = mFirebaseDatabaseReference.child("users/" + mFirebaseUser.getUid() );
        mUserNamesDatabase = mFirebaseDatabaseReference.child("user_names");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
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
                String sex = getSex();
                int age = getAge();

                if (age == -1) break;
                if(!validateUserInputs(userName, fullName)) break;

                User user = new User(userName, mFirebaseUser.getEmail(), fullName,
                        mFirebaseUser.getUid(), sex, age, mFirebaseUser.getUid(), true);

                saveUserIfNotExist(user);

                break;
            }
            case R.id.image_button_gallary: {
                if (hasMediaPermission()) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, REQUEST_PICK_PHOTO_GALLARY);
                }
            }
            default:
                break;
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
                    mUserNamesDatabase.child(user.getUserName()).setValue(true);
                    mUsersDatabase.setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private synchronized void uploadPhoto(String uid) {
        StorageReference photoRef = mStorageReference.child("images").child(uid);
        if (mImageUri != null) {
            photoRef.putFile(mImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Log.i("UPLOAD", downloadUri.toString());
                }
            });
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            photoRef.putBytes(data).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Log.i("UPLOAD", downloadUri.toString());
                }
            });
        }
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_PHOTO_GALLARY:
                if (resultCode == Activity.RESULT_OK) {
                    mImageUri = data.getData();
                    uploadPhoto(mFirebaseUser.getUid());
                } else {
                    mImageUri = null;
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getSex() {
        int radioId = mRadioGroupSex.getCheckedRadioButtonId();
        mRadioButtonSex = (RadioButton) findViewById(radioId);
        String sex = mRadioButtonSex.getText().toString();
        if (sex.equals("Male"))
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.male);
        else bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.female);
        return sex;
    }

    public int getAge() {
        String age = mEditTextAge.getText().toString().trim();
        if (TextUtils.isEmpty(age)) {
            mEditTextAge.setError("Enter age");
            return -1;
        }
        return Integer.parseInt(age);
    }

    private boolean hasMediaPermission() {
        String mediaPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, mediaPermission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, mediaPermission)) {
                    Snackbar.make(mEditTextAge, "Give Media Permission", Snackbar.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this, new String[]{mediaPermission},
                            REQUEST_PICK_PHOTO_GALLARY);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{mediaPermission},
                            REQUEST_PICK_PHOTO_GALLARY);
                }

                return false;
            }

            return true;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PICK_PHOTO_GALLARY: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mEditTextAge, "Permission not granted", Snackbar.LENGTH_SHORT).show();
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
