package com.example.rubel.u2uchat.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.Util.AppConstants;
import com.example.rubel.u2uchat.Util.AppUtils;
import com.example.rubel.u2uchat.adapter.FeaturesPagerAdapter;
import com.example.rubel.u2uchat.fragments.ChatFragment;
import com.example.rubel.u2uchat.fragments.ContactsFragment;
import com.example.rubel.u2uchat.fragments.GroupsFragment;
import com.example.rubel.u2uchat.fragments.SearchUserFragment;
import com.example.rubel.u2uchat.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PHOTO_PICKER = 101;
    private static final int REQUEST_SIGN_IN = 102;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 103;

    // UI elements
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    Toolbar mToolbar;

    // Firebase API Clients
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;
    private FirebaseUser mFirebaseUser;

    private String mAuthProvider;

    private List<Fragment> appFragments;

    private User mAppUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setTitle("U2U Chat");
        setSupportActionBar(mToolbar);

        checkInternetConnection();

        initFirebaseAuthAndUser();

        verifyLoginUser();

        initFirebaseDatabaseAndStorage();

        checkFirstTimeLogin();

        initAuthStateListener();

        makeUserOnline();

        setupNavigationView();

        setupTabsAndPagers();
    }

    private void checkInternetConnection() {
        if (!AppUtils.isConnectedToIntenet(this)) {
            Toast.makeText(this, "No intenet connection!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initFirebaseAuthAndUser() {
        mAppUser = null;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mAuthProvider = mFirebaseUser.getProviderData().get(0).toString();

        Log.i("PROVIDER:", mAuthProvider);
    }

    private void verifyLoginUser() {
        if (mFirebaseUser == null) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
    }

    // initialize firebase database and storage clients
    private void initFirebaseDatabaseAndStorage() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("users")
                .child(mFirebaseUser.getUid() + "/");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPhotoStorageReference = mFirebaseStorage.getReference().child("user_photos");
    }

    // check if user has a profile or not then create new one or set profile
    private void checkFirstTimeLogin() {
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    startActivity(new Intent(MainActivity.this, FirstTimeLoginActivity.class));
                    finish();
                } else {
                    setAppUser(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initAuthStateListener() {
        mFirebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser == null) {
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };
    }

    private void setupTabsAndPagers() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager_main);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout_main);
        initializeFragments();
        FeaturesPagerAdapter adapter = new FeaturesPagerAdapter(getSupportFragmentManager(),
                this.appFragments);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        addIconsToTab();
    }

    private void addIconsToTab() {
        mTabLayout.getTabAt(0).setIcon(R.drawable.account_search);
        mTabLayout.getTabAt(1).setIcon(R.drawable.wechat);
        mTabLayout.getTabAt(2).setIcon(R.drawable.account);
        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_group);

        final int tabColor = ContextCompat.getColor(this, R.color.tabColor);
        final int selectedColor = ContextCompat.getColor(this, R.color.colorAccent);

        mTabLayout.getTabAt(0).getIcon().setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN);
        mTabLayout.getTabAt(1).getIcon().setColorFilter(tabColor, PorterDuff.Mode.SRC_IN);
        mTabLayout.getTabAt(2).getIcon().setColorFilter(tabColor, PorterDuff.Mode.SRC_IN);
        mTabLayout.getTabAt(3).getIcon().setColorFilter(tabColor, PorterDuff.Mode.SRC_IN);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setCustomView(R.layout.view_tab);
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(tabColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupNavigationView() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view_main);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void initializeFragments() {
        appFragments = new ArrayList<>();
        appFragments.add(new SearchUserFragment());
        appFragments.add(new ChatFragment());
        appFragments.add(new ContactsFragment());
        appFragments.add(new GroupsFragment());
    }

    private void setAppUserProfile() {
        if (mAppUser == null)
            return;

        View header = mNavigationView.getHeaderView(0);
        TextView tvUserFullName = (TextView) header.findViewById(R.id.text_view_name_main);
        TextView tvUserEmail = (TextView) header.findViewById(R.id.text_view_email_main);
        tvUserFullName.setText(mAppUser.getFullName());
        tvUserEmail.setText(mAppUser.getEmail());
    }

    private void setAppUser(DataSnapshot dataSnapshot) {
        String userName = dataSnapshot.child("userName").getValue().toString();
        String email = dataSnapshot.child("email").getValue().toString();
        String fullName = dataSnapshot.child("fullName").getValue().toString();
        String uid = dataSnapshot.child("uid").getValue().toString();
        String photoUrl = dataSnapshot.child("photoUrl").getValue().toString();
        boolean isOnline = dataSnapshot.child("isOnline").getValue().toString().equals("true");

        mAppUser = new User(userName, email, fullName, uid, photoUrl, isOnline);

        setAppUserProfile();

        setUserToPreference();
    }

    private void setUserToPreference() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.APP_PREFERENCE,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String userString = gson.toJson(mAppUser, User.class);
        editor.putString(AppConstants.APP_USER, userString);
        editor.apply();
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
        mFirebaseDatabaseReference.child("isOnline").setValue(false);
    }

    private void makeUserOnline() {
        mFirebaseDatabaseReference.child("isOnline").setValue(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFirebaseAuth != null)
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthStateListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public User getmAppUser() {
        return this.mAppUser;
    }
}