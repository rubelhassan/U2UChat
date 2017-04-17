package com.example.rubel.u2uchat.fragments;

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
import android.widget.Toast;

import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.adapter.ChatsListAdapter;
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
                Toast.makeText(getContext(), "Shei Click Hoise", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {

                    // check if connection is online then proceed
                    mFirebaseDatabase.getReference().child("users")
                            .child(dataSnapshot.child("senderId").getValue().toString())
                            .child("isOnline")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnap) {
                                    trackOnline = dataSnap.getValue().toString().equals("true");
                                    Log.i("IS_ONLINE:", dataSnap.toString());

                                    addConnectionToRecyclerList(dataSnapshot);
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

        mFirebaseDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void addConnectionToRecyclerList(DataSnapshot dataSnapshot) {
        UserConnection connection = new UserConnection(
                dataSnapshot.child("name").getValue().toString(),
                dataSnapshot.child("messageId").getValue().toString(),
                dataSnapshot.child("senderId").getValue().toString(),
                dataSnapshot.child("lastMessage").getValue().toString(),
                Long.valueOf(dataSnapshot.child("timestamps").getValue().toString()),
                dataSnapshot.child("photoUrl").getValue().toString(),
                trackOnline);

        mUserConnections.add(connection);
        mConnectionAdapter.notifyDataSetChanged();
    }

    private void initFirebaseAuthAndUser() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    private void initFirebaseDatabaseAndStorage() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("users_connections")
                .child(mFirebaseUser.getUid() + "/");
    }
}
