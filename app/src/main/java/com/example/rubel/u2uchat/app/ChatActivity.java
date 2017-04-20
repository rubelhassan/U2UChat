package com.example.rubel.u2uchat.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.Util.AppConstants;
import com.example.rubel.u2uchat.adapter.MessageListAdapter;
import com.example.rubel.u2uchat.model.Message;
import com.example.rubel.u2uchat.model.User;
import com.example.rubel.u2uchat.model.UserConnection;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by rubel on 4/17/2017.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    User mUserReceiver;
    User mCurrentUser;

    // UI elements
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    ImageButton mImageButtonEmoji;
    ImageButton mImageButtonSend;
    EmojiconEditText mEditTextMessage;
    EmojIconActions mEmojIcon;
    View mRootView;

    MessageListAdapter mMessageListAdapter;
    List<Message> mMessageList;

    FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mSenderMessageReference;
    DatabaseReference mReceiverMessageReference;
    DatabaseReference mGlobalMessageReference;
    ChildEventListener mMessaseChildEventListener;

    String messageId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setAppUser();

        initReceiverUser();

        setToolbar();

        initViews();

        setRecyclerAdapter();

        initFirebaseDatabase();

        initMessageChildListener();
    }

    private void initMessageChildListener() {
        mMessaseChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    Message message = new Message(
                            dataSnapshot.child("content").getValue().toString(),
                            dataSnapshot.child("sender").getValue().toString(),
                            dataSnapshot.child("receiver").getValue().toString(),
                            dataSnapshot.child("text").getValue().toString().equals("true"),
                            dataSnapshot.child("sender").getValue().toString().equals(mUserReceiver.getUid()),
                            Long.valueOf(dataSnapshot.child("timestamps").getValue().toString()));
                    mMessageList.add(message);
                    mMessageListAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mMessageListAdapter.getItemCount());
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

    private void initFirebaseDatabase() {
        messageId = getMessageId();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mSenderMessageReference = mFirebaseDatabase.getReference().child("users_connections")
                .child(mCurrentUser.getUid());
        mReceiverMessageReference = mFirebaseDatabase.getReference().child("users_connections")
                .child(mUserReceiver.getUid());
        mGlobalMessageReference = mFirebaseDatabase.getReference().child("messages")
                .child(messageId);
    }

    private void setAppUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.APP_PREFERENCE,
                MODE_PRIVATE);
        Gson gson = new Gson();
        String userString = sharedPreferences.getString(AppConstants.APP_USER, "N/A");
        mCurrentUser = gson.fromJson(userString, User.class);

        Log.i("USER_CHAT", mCurrentUser.getEmail());
    }

    private void setRecyclerAdapter() {
        mMessageList = new ArrayList<>();
        mMessageListAdapter = new MessageListAdapter(mMessageList, this,
                mCurrentUser.getPhotoUrl(), mUserReceiver.getPhotoUrl());
        mRecyclerView.setAdapter(mMessageListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        mToolbar.setTitle(mUserReceiver.getFullName());
        mToolbar.setNavigationIcon(R.drawable.arrow_left);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.super.onBackPressed();
            }
        });
    }

    private void initViews() {
        mRootView = findViewById(R.id.root_layout_chat);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_chat);
        mEditTextMessage = (EmojiconEditText) findViewById(R.id.edit_text_chat_content);
        mImageButtonEmoji = (ImageButton) findViewById(R.id.image_button_chat_emoicon);
        mImageButtonSend = (ImageButton) findViewById(R.id.image_button_chat_send);
        mImageButtonSend.setOnClickListener(this);
        mEmojIcon = new EmojIconActions(this, mRootView, mEditTextMessage, mImageButtonEmoji);
        mEmojIcon.setIconsIds(R.drawable.keyboard, R.drawable.emoticon);
        mEmojIcon.ShowEmojIcon();
    }

    private void initReceiverUser() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUserReceiver = (User) extras.getSerializable(AppConstants.CHAT_ACTIVITY_RECEIVER);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_button_chat_send) {
            Log.i("ONCLICK", "image button chat");
            String content = mEditTextMessage.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                return;
            }

            long time = System.currentTimeMillis();
            Message message = new Message(content, mCurrentUser.getUid(), mUserReceiver.getUid(),
                    true, false, time);
            UserConnection senderConnection = new UserConnection(mCurrentUser.getFullName(), messageId,
                    mCurrentUser.getUid(), content, time, mCurrentUser.getPhotoUrl(), false);

            UserConnection receiverConnection = new UserConnection(mUserReceiver.getFullName(), messageId,
                    mCurrentUser.getUid(), content, time, mUserReceiver.getPhotoUrl(), false);

            mGlobalMessageReference.push().setValue(message);

            mSenderMessageReference.child(mUserReceiver.getUid()).setValue(receiverConnection);
            mReceiverMessageReference.child(mCurrentUser.getUid()).setValue(senderConnection);

            mEditTextMessage.setText(null);
        }
    }

    public String getMessageId() {
        String first = mCurrentUser.getUserName();
        String second = mUserReceiver.getUserName();
        int comp = first.compareTo(second);
        if (comp < 0) return first + "_" + second;
        return second + "_" + first;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGlobalMessageReference.addChildEventListener(mMessaseChildEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGlobalMessageReference != null)
            mGlobalMessageReference.removeEventListener(mMessaseChildEventListener);
        mMessageList.clear();
        mMessageListAdapter.notifyDataSetChanged();
    }
}
