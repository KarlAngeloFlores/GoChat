package com.example.gochat.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochat.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    ArrayList<MessageObject> messageListArray;


    public MessageAdapter(ArrayList<MessageObject> MessageArray) {
        this.messageListArray = MessageArray;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false); //note
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.mMessage.setText(messageListArray.get(position).getMessage());
        holder.mSender.setText(messageListArray.get(position).getSenderId() );
    }

    @Override
    public int getItemCount() {
        return messageListArray.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView mMessage, mSender;


        public LinearLayout mLayout;
        public MessageViewHolder(View view){
            super(view);

            mMessage = view.findViewById(R.id.messageEt);
            mSender = view.findViewById(R.id.sender);

            mLayout = view.findViewById(R.id.layout);
        }
    }
}