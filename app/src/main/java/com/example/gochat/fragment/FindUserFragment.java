package com.example.gochat.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochat.CountryToPhonePrefix;
import com.example.gochat.R;
import com.example.gochat.UserListAdapter;
import com.example.gochat.UserObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FindUserFragment extends Fragment {

    private RecyclerView userListRv; //my recycle view
    private RecyclerView.Adapter userListAdapter; //adapter

    Button mCreate;

    private RecyclerView.LayoutManager userListLayoutManager; //layout manager
    ArrayList<UserObject> userListArray, contactListArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_user, container, false);

        mCreate = view.findViewById(R.id.btnGroupChat);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat();
            }
        });

        contactListArray = new ArrayList<>(); //list contains all contacts
        userListArray = new ArrayList<>(); //list contains all users on database

        //function calls
        initializeRecycleView(view); //called function for recycler view
        getContactList(); //called function contact list

        return view;
    } //end bracket for main body

    private String getCountryISO() {
        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null)
            if (!telephonyManager.getNetworkCountryIso().isEmpty())
                iso = telephonyManager.getNetworkCountryIso();
        return CountryToPhonePrefix.getPhone(iso);
    }


    private void createChat(){
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");

        HashMap newChatMap = new HashMap();
        newChatMap.put("id", key);
        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);

        Boolean validChat = false;
        for(UserObject mUser : userListArray){
            if(mUser.getSelected()){
                validChat = true;
                newChatMap.put("users/" + mUser.getUid(), true);
                userDb.child(mUser.getUid()).child("chat").child(key).setValue(true);
            }
        }

        if(validChat){
            chatInfoDb.updateChildren(newChatMap);
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        }
    }

    private void getContactList() {
        String ISOPrefix = getCountryISO();
        Cursor phones = requireContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            try {
                while (phones.moveToNext()) {
                    @SuppressLint("Range") String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    @SuppressLint("Range") String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phone = phone.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");
                    if (!phone.startsWith("+"))
                        phone = ISOPrefix + phone;
                    UserObject mContact = new UserObject("", name, phone);
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
                if (dataSnapshot.exists()) {
                    String phone = "", name = "";
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();
                        if (childSnapshot.child("name").getValue() != null)
                            name = childSnapshot.child("name").getValue().toString();
                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, phone);
                        if (name.equals(phone))
                            for (UserObject mContactIterator : contactListArray) {
                                if (mContactIterator.getPhone().equals(mUser.getPhone())) {
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

    private void initializeRecycleView(View view) {
        userListRv = view.findViewById(R.id.rvUserList);
        userListRv.setNestedScrollingEnabled(false);
        userListRv.setHasFixedSize(true);

        userListLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        userListRv.setLayoutManager(userListLayoutManager);

        userListAdapter = new UserListAdapter(userListArray);
        userListRv.setAdapter(userListAdapter);
    }
} //end bracket
