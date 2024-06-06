package com.example.gochat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getExtras().getString("chatID");

        btnSend = findViewById(R.id.send);
        mMessage = findViewById(R.id.messageEt);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //function calls
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

    private void initializeRecyclerView() {

        chatRv = findViewById(R.id.rvMessage);
        chatRv.setNestedScrollingEnabled(false);
        chatRv.setHasFixedSize(true);

        chatLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatRv.setLayoutManager(chatLayoutManager);

        chatAdapter = new MessageAdapter(messageListArray);
        chatRv.setAdapter(chatAdapter);

    }

}