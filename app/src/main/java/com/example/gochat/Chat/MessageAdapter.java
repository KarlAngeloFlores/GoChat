package com.example.gochat.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochat.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    ArrayList<MessageObject> messageListArray;
    private String currentUserId;

    FirebaseUser currentUser;


    public MessageAdapter(ArrayList<MessageObject> messageArray, String currentUserId) {
        this.messageListArray = messageArray;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new MessageViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageObject messageObject = messageListArray.get(position);

        if (messageObject.getSenderId().equals(currentUserId)) {
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatTextViewMessage.setText(messageObject.getMessage());
            //holder.rightChatTextViewCreator.setText(messageObject.getSenderId());
        } else {
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatTextViewMessage.setText(messageObject.getMessage());
            //holder.leftChatTextViewCreator.setText(messageObject.getSenderId());
        }
/*
        holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> mediaUrls = messageListArray.get(holder.getAdapterPosition()).getMediaUrlList();
                if (mediaUrls != null && !mediaUrls.isEmpty()) {
                    new ImageViewer.Builder(v.getContext(), mediaUrls)
                            .setStartPosition(0)
                            .show();
                }
            }
        });

 */
    }

    @Override
    public int getItemCount() {
        return messageListArray.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout, rightChatLayout;
        TextView leftChatTextViewMessage, leftChatTextViewCreator, rightChatTextViewMessage, rightChatTextViewCreator;

        Button mViewMedia;

        public MessageViewHolder(View view) {
            super(view);

            leftChatLayout = view.findViewById(R.id.left_chat_layout);
            rightChatLayout = view.findViewById(R.id.right_chat_layout);
            leftChatTextViewMessage = view.findViewById(R.id.left_chat_textview_message);
            //leftChatTextViewCreator = view.findViewById(R.id.left_chat_textview_creator);
            rightChatTextViewMessage = view.findViewById(R.id.right_chat_textview_message);
            //rightChatTextViewCreator = view.findViewById(R.id.right_chat_textview_creator);


        }
    }
}
