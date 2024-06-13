package com.example.gochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    ArrayList<UserObject> userListArray;

    public UserListAdapter(ArrayList<UserObject> userListArray) {
        this.userListArray = userListArray;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }


    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, final int position) {
        holder.name.setText(userListArray.get(position).getName());
        holder.phone.setText(userListArray.get(position).getPhone());

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat(holder.getAdapterPosition());
            }
        });


        holder.mAdd.setOnCheckedChangeListener(null);
        holder.mAdd.setChecked(userListArray.get(position).getSelected());

        holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userListArray.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });

    }


    private void createChat(int position) {
        String currentUserUid = FirebaseAuth.getInstance().getUid();
        String otherUserPhone = userListArray.get(position).getPhone();

        String chatId = generateChatId(currentUserUid, otherUserPhone);
        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserUid).child("chat").child(chatId).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("user").child(userListArray.get(position).getUid()).child("chat").child(chatId).setValue(true);
    }

    private String generateChatId(String currentUserUid, String otherUserPhone) {
        String[] phones = {currentUserUid, otherUserPhone};
        Arrays.sort(phones);
        return phones[0] + "_" + phones[1];
    }

    @Override
    public int getItemCount() {
        return userListArray.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone;
        public LinearLayout mLayout;
        CheckBox mAdd;
    public UserListViewHolder(View view) {
        super(view); //comes first

        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        mLayout = view.findViewById(R.id.layout);
        mAdd = view.findViewById(R.id.addToGroup);

        }
    }
}