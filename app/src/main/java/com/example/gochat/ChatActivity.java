package com.example.gochat;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gochat.Chat.MediaAdapter;
import com.example.gochat.Chat.MessageAdapter;
import com.example.gochat.Chat.MessageObject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRv, mediaRv; // my RecyclerView
    private RecyclerView.Adapter chatAdapter, mediaAdapter; // adapter
    private RecyclerView.LayoutManager chatLayoutManager, mediaLayoutManager; // layout manager
    ArrayList<MessageObject> messageListArray = new ArrayList<>();

    TextView displayName;

    ImageView btnSend;
    ImageView btnAddMedia;

    EditText mMessage;

    String chatId;

    DatabaseReference mChatDb;

    String currentUserId;

    ImageView btnBackToMainPage;
    DatabaseReference newMessageDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnBackToMainPage = findViewById(R.id.ivBackMainPage);
        displayName = findViewById(R.id.tvDisplayName);
        btnAddMedia = findViewById(R.id.mAddMedia);
        chatId = getIntent().getExtras().getString("chatID");

        btnBackToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });

        btnAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        String nameDisplay = getIntent().getStringExtra("DISPLAYNAME");
        displayName.setText(nameDisplay);

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

        // Function calls
        getChatMessages();
        initializeMessages();
        initializeMedia();
    }

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();

    private void sendMessage() {
        String messageId = mChatDb.push().getKey();
        DatabaseReference newMessage = mChatDb.child(messageId);

        final Map<String, Object> newMessageMap = new HashMap<>();
        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

        if (!mMessage.getText().toString().isEmpty())
            newMessageMap.put("text", mMessage.getText().toString());

        if (!mediaUriList.isEmpty()) {
            for (String mediaUri : mediaUriList) {
                String mediaId = newMessageDb.child("media").push().getKey();
                mediaIdList.add(mediaId);
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatId).child(messageId).child(mediaId);
                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());

                                totalMediaUploaded++;
                                if (totalMediaUploaded == mediaIdList.size()) {
                                    updateDatabaseWithNewMessage(newMessage, newMessageMap);
                                }
                            }
                        });
                    }
                });
            }
        } else {
            if (!mMessage.getText().toString().isEmpty())
                updateDatabaseWithNewMessage(newMessage, newMessageMap);
        }

        mMessage.setText(null);
    }

    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map<String, Object> newMessageMap) {
        newMessageDb.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        mediaAdapter.notifyDataSetChanged();
    }

    private void getChatMessages() {
        mChatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String text = "", creatorId = "";

                    if (snapshot.child("text").getValue() != null) {
                        text = snapshot.child("text").getValue().toString();
                    }

                    if (snapshot.child("creator").getValue() != null) {
                        creatorId = snapshot.child("creator").getValue().toString();
                    }

                    MessageObject mMessage = new MessageObject(snapshot.getKey(), creatorId, text);

                    messageListArray.add(mMessage);
                    chatLayoutManager.scrollToPosition(messageListArray.size() - 1); // Scroll to the last message
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

    private void initializeMessages() {
        chatRv = findViewById(R.id.rvMessage);
        chatRv.setNestedScrollingEnabled(false);
        chatRv.setHasFixedSize(true);

        chatLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatRv.setLayoutManager(chatLayoutManager);

        chatAdapter = new MessageAdapter(messageListArray, currentUserId); // Pass current user ID
        chatRv.setAdapter(chatAdapter);
    }

    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        mediaRv = findViewById(R.id.mediaList);
        mediaRv.setNestedScrollingEnabled(false);
        mediaRv.setHasFixedSize(true);

        mediaLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mediaRv.setLayoutManager(mediaLayoutManager);

        mediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mediaRv.setAdapter(mediaAdapter);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_INTENT) {
                if (data.getClipData() == null) {
                    mediaUriList.add(data.getData().toString());
                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mediaAdapter.notifyDataSetChanged();
            }
        }
    }
}
