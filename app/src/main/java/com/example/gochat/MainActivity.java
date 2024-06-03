package com.example.gochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText phoneNumber, verificationCode;

    Button buttonVerification;
    Long timeOutSeconds = 120L;
    FirebaseAuth mAuth;
    String sentVerificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    boolean receivedOTP;

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

        buttonVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sentVerificationCode != null) {
                    verifyPhoneNumberWithCode();
                } else {
                    sendOtp(phoneNumber.getText().toString());
                }

            }
        }); //end bracket for event listener

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