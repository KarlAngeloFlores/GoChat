package com.example.gochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUser extends AppCompatActivity {

    private RecyclerView userListRv; //my recycle view
    private RecyclerView.Adapter userListAdapter; //adapter
    DatabaseReference userDatabase; //my database reference

    private RecyclerView.LayoutManager userListLayoutManager; //layout manager

    ArrayList<UserObject> userListArray, contactListArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        contactListArray = new ArrayList<>(); //list contains all contacts
        userListArray = new ArrayList<>(); //list contains all users on database

        //function calls
        initializeRecycleView(); //called function for recycler view
        getContactList(); //called function contact list

    } //end bracket for main body

    private String getCountryISO(){
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }

    private void getContactList() {
        String ISOPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            try {
                while (phones.moveToNext()) {
                    @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    @SuppressLint("Range") String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phone = phone.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");

                    if (!phone.startsWith("+"))
                        phone = ISOPrefix + phone;

                    UserObject mContact = new UserObject(name, phone);
                    contactListArray.add(mContact);
                    getUserDetails(mContact);
                }
            } finally {
                phones.close(); //Cursor Closed
            }
        }
    }
    //end bracket

    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  phone = "",
                            name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("phone").getValue()!=null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if(childSnapshot.child("name").getValue()!=null)
                            name = childSnapshot.child("name").getValue().toString();


                        UserObject mUser = new UserObject(name, phone);
                        if (name.equals(phone))
                            for(UserObject mContactIterator : contactListArray){
                                if(mContactIterator.getPhone().equals(mUser.getPhone())){
                                    mUser.setName(mContactIterator.getName());
                                }
                            }

                        userListArray.add(mUser);
                        userListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeRecycleView() {
        userListRv = findViewById(R.id.rvUserList);
        userListRv.setNestedScrollingEnabled(false);
        userListRv.setHasFixedSize(true);

        userListLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userListRv.setLayoutManager(userListLayoutManager);

        userListAdapter = new UserListAdapter(userListArray);
        userListRv.setAdapter(userListAdapter);
    }
} //end bracket