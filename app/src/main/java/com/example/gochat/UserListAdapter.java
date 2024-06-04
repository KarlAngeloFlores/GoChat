package com.example.gochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {

        holder.name.setText(userListArray.get(position).getName());
        holder.phone.setText(userListArray.get(position).getPhone());

    }

    @Override
    public int getItemCount() {
        return userListArray.size();
    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone;
    public UserListViewHolder(View view) {
        super(view); //comes first

        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);

        }
    }
}