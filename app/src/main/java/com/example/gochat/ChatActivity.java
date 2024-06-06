package com.example.gochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.gochat.Chat.MessageAdapter;
import com.example.gochat.Chat.MessageObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRv; //my recycle view
    private RecyclerView.Adapter chatAdapter; //adapter
    private RecyclerView.LayoutManager chatLayoutManager; //layout manager
    ArrayList<MessageObject> messageListArray = new ArrayList<>();



    Button btnSend;
    EditText mMessage;

    String chatId;

    DatabaseReference mChatDb;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getExtras().getString("chatID");

        currentUserId = FirebaseAuth.getInstance().getUid();

        btnSend = findViewById(R.id.send);
        mMessage = findViewById(R.id.messageEt);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mChatDb = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId);

        //function calls
        getChatMessages();
        initializeRecyclerView();

    }

    private void sendMessage() {
        if(!mMessage.getText().toString().isEmpty()) {
            DatabaseReference newMessageDb = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId).push();

            Map newMessageMap = new HashMap<>();
            newMessageMap.put("text", mMessage.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

            newMessageDb.updateChildren(newMessageMap);
        }

        mMessage.setText(null);


    }

    private void getChatMessages() {
        mChatDb.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()) {
                    String text = "", creatorId = "";

                    if(snapshot.child("text").getValue() != null) {
                        text = snapshot.child("text").getValue().toString();
                    }

                    if(snapshot.child("creator").getValue() != null) {
                        creatorId = snapshot.child("creator").getValue().toString();
                    }

                    MessageObject mMessage = new MessageObject(snapshot.getKey(), creatorId, text);

                    messageListArray.add(mMessage);
                    chatLayoutManager.scrollToPosition(messageListArray.size() - 1); //scroll to the last message
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initializeRecyclerView() {
        chatRv = findViewById(R.id.rvMessage);
        chatRv.setNestedScrollingEnabled(false);
        chatRv.setHasFixedSize(true);

        chatLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatRv.setLayoutManager(chatLayoutManager);

        chatAdapter = new MessageAdapter(messageListArray, currentUserId); // Pass current user ID
        chatRv.setAdapter(chatAdapter);
    }

}