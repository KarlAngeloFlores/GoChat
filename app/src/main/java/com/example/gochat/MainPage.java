package com.example.gochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.gochat.Chat.ChatListAdapter;
import com.example.gochat.Chat.ChatObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity {
    Button logoutButton, contactsButton;

    private RecyclerView chatListRv; //my recycle view
    private RecyclerView.Adapter chatListAdapter; //adapter
    private RecyclerView.LayoutManager chatListLayoutManager; //layout manager
    ArrayList<ChatObject> chatListArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        logoutButton = findViewById(R.id.btnLogout);
        contactsButton = findViewById(R.id.btnFindUsers);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent((getApplicationContext()), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FindUser.class));
            }
        });

        getPermissions(); //get permissions
        initializeRecycleView(); //initialize View
        getUserChatList();
    } //end of main body bracket

    private void getUserChatList() {
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");

        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        boolean  exists = false;
                        for (ChatObject mChatIterator : chatListArray){
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


    private void initializeRecycleView() {
        chatListRv = findViewById(R.id.rvChatList);
        chatListRv.setNestedScrollingEnabled(false);
        chatListRv.setHasFixedSize(true);

        chatListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatListRv.setLayoutManager(chatListLayoutManager);

        chatListAdapter = new ChatListAdapter(chatListArray);
        chatListRv.setAdapter(chatListAdapter);
    }

    private void getPermissions() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }

    }

} //end bracket