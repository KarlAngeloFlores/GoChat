package com.example.gochat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FindUser extends AppCompatActivity {


    private RecyclerView userList;
    private RecyclerView.Adapter userListAdapter;
    private RecyclerView.LayoutManager userListLayoutManager;

    ArrayList<UserObject> userListArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        userListArray = new ArrayList<>();

        //calls
        initializeRecycleView(); //called the function
        getContactList(); //called the function

    } //end bracket for main body


    private void getContactList() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while(phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

            UserObject contacts = new UserObject(name, phone);
            userListArray.add(contacts);
            userListAdapter.notifyDataSetChanged();


        }


    }

    private void initializeRecycleView() {
        userList = findViewById(R.id.rvUserList);
        userList.setNestedScrollingEnabled(false);
        userList.setHasFixedSize(false);

        userListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false); //not sure about LinearLayoutManager
        userList.setLayoutManager(userListLayoutManager);
        userListAdapter = new UserListAdapter(userListArray);
        userList.setAdapter(userListAdapter);

    }
}