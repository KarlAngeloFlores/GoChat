package com.example.gochat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText phoneNumber, verificationCode;

    Button buttonVerification, buttonSendCode;
    Long timeOutSeconds = 120L;
    FirebaseAuth mAuth;
    String sentVerificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    //ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);


        mAuth = FirebaseAuth.getInstance();

        userIsLoggedIn();

        phoneNumber = findViewById(R.id.etPhoneNumber);
        verificationCode = findViewById(R.id.etVerificationCode);
        buttonVerification = findViewById(R.id.btnVerifyButton);
        buttonSendCode = findViewById(R.id.btnSendCode);
        buttonVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sentVerificationCode != null) {
                    verifyPhoneNumberWithCode();
                } else {
                    buttonVerification.setText("Incorrect code");
                }

            }
        }); //end bracket for event listener

        buttonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp(phoneNumber.getText().toString());
            }
        });

    } //main bracket for body
    private void sendOtp(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeOutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneNumber(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        sentVerificationCode = s;
                        resendingToken = forceResendingToken;
                        Toast.makeText(MainActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        buttonVerification.setText("Verify Code");

                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);  // Start the OTP verification
    }
    //end bracket

    void signInWithPhoneNumber(PhoneAuthCredential phoneAuthCredential) {

        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user != null) {
                        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()) {
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("phone", user.getPhoneNumber());
                                    userMap.put("name", user.getPhoneNumber());
                                    mUserDB.updateChildren(userMap);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("FindUser", "Database Error: " + error.getMessage());
                            }
                        });
                    }
                    userIsLoggedIn();
                }
            }
        });

    } //end bracket

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            startActivity(new Intent(getApplicationContext(), MainPage.class));
            return;
        }
    }

    private void verifyPhoneNumberWithCode() {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sentVerificationCode, verificationCode.getText().toString());
        signInWithPhoneNumber(credential);
    }
}