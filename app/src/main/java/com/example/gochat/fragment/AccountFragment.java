package com.example.gochat.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gochat.MainActivity;
import com.example.gochat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    Button logoutButton;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TextView currentPhoneNumber;

    String currentUser = user.getPhoneNumber();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        logoutButton = view.findViewById(R.id.btnLogout);
        currentPhoneNumber = view.findViewById(R.id.tvCurrentPhone);
        currentPhoneNumber.setText(currentUser);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent((getContext()), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                return;
            }
        });


        return view;
    }
}