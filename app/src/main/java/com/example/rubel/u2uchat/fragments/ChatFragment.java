package com.example.rubel.u2uchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.Util.AppConstants;
import com.example.rubel.u2uchat.adapter.ChatsListAdapter;
import com.example.rubel.u2uchat.app.ChatActivity;
import com.example.rubel.u2uchat.model.User;
import com.example.rubel.u2uchat.model.UserConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rubel on 4/14/2017.
 */

public class ChatFragment extends Fragment {

    ChatsListAdapter mConnectionAdapter;
    List<UserConnection> mUserConnections;
    List<String> mConnectionsUid;

    //Firebase API Clients
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFirebaseDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private boolean trackOnline = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserConnections = new ArrayList<>();
        mConnectionsUid = new ArrayList<>();
        mConnectionAdapter = new ChatsListAdapter(mUserConnections, getContext());

        setAdapterListener();

        initFirebaseAuthAndUser();

        initFirebaseDatabaseAndStorage();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        setRecyclerViewAdapter(view);

        attachDatabaseListener();

        return view;
    }

    private void setRecyclerViewAdapter(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_fragment_chat_user);
        recyclerView.setAdapter(mConnectionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void setAdapterListener() {
        mConnectionAdapter.setChatOnItemClickListener(new ChatsListAdapter.ChatOnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                mFirebaseDatabase.getReference().child("users")
                        .child(mConnectionsUid.get(position))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = new User(
                                        dataSnapshot.child("userName").getValue().toString(),
                                        dataSnapshot.child("email").getValue().toString(),
                                        dataSnapshot.child("fullName").getValue().toString(),
                                        dataSnapshot.child("uid").getValue().toString(),
                                        dataSnapshot.child("photoUrl").getValue().toString(),
                                        dataSnapshot.child("isOnline").getValue().toString().equals("true"));
                                startChatWithUser(user);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    private void startChatWithUser(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(AppConstants.CHAT_ACTIVITY_RECEIVER, user);
        startActivity(intent);
    }

    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot lastMessageSnapshot, String s) {

                    Log.i("LOG_DATA", lastMessageSnapshot.getKey() + " -- " + lastMessageSnapshot.toString());
                    // check if connection is online then proceed
                    mFirebaseDatabase.getReference().child("users")
                            .child(lastMessageSnapshot.getKey())
                            .child("isOnline")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnap) {
                                    trackOnline = dataSnap.getValue().toString().equals("true");
                                    Log.i("IS_ONLINE:", dataSnap.toString());
                                    addConnectionToRecyclerList(lastMessageSnapshot, lastMessageSnapshot.getKey());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


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
    }

    private void addConnectionToRecyclerList(DataSnapshot lastMessageSnapshot, String connectionKey) {
        String message = lastMessageSnapshot.child("lastMessage").getValue().toString();
        if (!lastMessageSnapshot.child("senderId").getValue().toString()
                .equals(connectionKey))
            message = "You:" + message;

        UserConnection connection = new UserConnection(
                lastMessageSnapshot.child("name").getValue().toString(),
                lastMessageSnapshot.child("messageId").getValue().toString(),
                lastMessageSnapshot.child("senderId").getValue().toString(),
                message,
                Long.valueOf(lastMessageSnapshot.child("timestamps").getValue().toString()),
                lastMessageSnapshot.child("photoUrl").getValue().toString(),
                trackOnline);

        mUserConnections.add(connection);
        mConnectionsUid.add(connectionKey);
        mConnectionAdapter.notifyDataSetChanged();
    }

    private void initFirebaseAuthAndUser() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    private void initFirebaseDatabaseAndStorage() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("users_connections")
                .child(mFirebaseUser.getUid());
    }

    @Override
    public void onPause() {
        super.onPause();
        mUserConnections.clear();
        mConnectionAdapter.notifyDataSetChanged();
        if (mFirebaseDatabaseReference != null)
            mFirebaseDatabaseReference.removeEventListener(mChildEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseDatabaseReference.addChildEventListener(mChildEventListener);
    }
}
