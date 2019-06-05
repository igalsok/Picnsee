package com.example.spotfacetest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    String phoneNumber;
    private EditText enterPhone;
    private Button sendSms;
    private EditText enterCode;
    private Button verify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private FirebaseAuth mAuth;
    private TextView error;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private int resend = 0;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_auth);
        db = FirebaseFirestore.getInstance();
        enterPhone = (EditText) findViewById(R.id.plnTxt_enterPhone);
        mAuth = FirebaseAuth.getInstance();
        sendSms = (Button) findViewById(R.id.btn_sndSms);
        enterCode = (EditText) findViewById(R.id.plnTxt_enterCode);
        error = (TextView) findViewById(R.id.txt_error);
        verify = (Button) findViewById(R.id.btn_verify);
        sendSms.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(resend == 0) {
                    enterPhone.setEnabled(false);
                    sendSms.setText("Resend");
                    enterCode.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.VISIBLE);
                    phoneNumber = enterPhone.getText().toString();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60 ,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            AuthActivity.this,               // Activity (for callback binding)
                            mCallBacks   // OnVerificationStateChangedCallbacks
                    );
                    resend = 1;
                }
                else
                {

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            AuthActivity.this,               // Activity (for callback binding)
                            mCallBacks      // OnVerificationStateChangedCallbacks
                            );             // ForceResendingToken from callbacks
                }
                }

        });
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                error.setText("Wrong code");
                error.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }

        };
        verify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
              String verificationCode  = enterCode.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            CollectionReference userRef = db.collection("Users");
                            userRef.whereEqualTo("phoneNumber", user.getPhoneNumber()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(!queryDocumentSnapshots.getDocuments().isEmpty()){
                                        Intent authIntent = new  Intent(AuthActivity.this,MainActivity.class);
                                        startActivity(authIntent);
                                        finish();
                                    }
                                    else{
                                        Intent authIntent = new  Intent(AuthActivity.this,RegActivity.class);
                                        startActivity(authIntent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            // Sign in failed, display a message and update the UI
                            error.setText("Wrong code");
                            error.setVisibility(View.VISIBLE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
