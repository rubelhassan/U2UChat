package com.example.rubel.u2uchat.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.Util.AppConstants;
import com.example.rubel.u2uchat.model.User;

/**
 * Created by rubel on 4/15/2017.
 */

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mTextViewName;
    ImageView mImageViewProfileThumb;
    ImageButton mImageButtonChat;
    User mUser;
    Toolbar mToolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setupToolbar();

        initViews();

        extractUserFromIntent();
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        mToolbar.setTitle("Back");
        mToolbar.setNavigationIcon(R.drawable.arrow_left);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.super.onBackPressed();
            }
        });
    }

    private void extractUserFromIntent() {
        Bundle extras = getIntent().getExtras();
        mUser = null;
        if (extras != null) {
            mUser = (User) extras.getSerializable(AppConstants.PROFILE_USER);
            setUserProfile();
        }
    }

    private void setUserProfile() {
        mTextViewName.setText(mUser.getFullName());
        Glide.with(getApplicationContext())
                .load(mUser.getPhotoUrl())
                .into(mImageViewProfileThumb);
    }

    private void initViews() {
        mTextViewName = (TextView) findViewById(R.id.text_view_profile_name);
        mImageButtonChat = (ImageButton) findViewById(R.id.image_button_profile_chat);
        mImageViewProfileThumb = (ImageView) findViewById(R.id.image_view_profile_thumb);

        mImageButtonChat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_button_profile_chat) {
            Snackbar.make(v, "chat button pressed", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
            intent.putExtra(AppConstants.CHAT_ACTIVITY_RECEIVER, mUser);
            startActivity(intent);
            finish();
        }
    }
}
