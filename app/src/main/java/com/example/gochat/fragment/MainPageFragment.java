package com.example.gochat.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import com.example.gochat.Chat.ChatListAdapter;
import com.example.gochat.Chat.ChatObject;
import com.example.gochat.FindUser;
import com.example.gochat.MainActivity;

import com.example.gochat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainPageFragment extends Fragment  {



    private RecyclerView chatListRv; //my recycle view
    private RecyclerView.Adapter chatListAdapter; //adapter
    private RecyclerView.LayoutManager chatListLayoutManager; //layout manager
    ArrayList<ChatObject> chatListArray = new ArrayList<>();

    FirebaseUser currentUser;

    Button showCurrent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        getPermissions(); //get permissions
        initializeRecycleView(view); //initialize View
        getUserChatList(); //getUser Chat List

        return view;
    }

    private void getUserChatList() {
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");

        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        boolean exists = false;
                        for (ChatObject mChatIterator : chatListArray) {
                            if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                exists = true;
                        }
                        if (exists)
                            continue;
                        chatListArray.add(mChat);
                    }
                    chatListAdapter.notifyDataSetChanged(); // Notify adapter here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeRecycleView(View view) {
        chatListRv = view.findViewById(R.id.rvChatList);
        chatListRv.setNestedScrollingEnabled(false);
        chatListRv.setHasFixedSize(true);

        chatListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        chatListRv.setLayoutManager(chatListLayoutManager);

        chatListAdapter = new ChatListAdapter(chatListArray);
        chatListRv.setAdapter(chatListAdapter);
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }

}
