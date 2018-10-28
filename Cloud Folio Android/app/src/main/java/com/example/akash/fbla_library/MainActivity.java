package com.example.akash.fbla_library;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button signInButton;
    private EditText emailField;
    private EditText passwordField;
    private FirebaseAuth firebaseAuth;
    private TextView createAccountText;
    private FirebaseDatabase fb;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("CloudFolio");

        firebaseAuth = FirebaseAuth.getInstance();
        fb = FirebaseDatabase.getInstance();
        databaseReference = fb.getReference().child("Users");

        assignUiID();
        initiateLoginProcess();
        logInUser();
        createAccount();

    }

    private void updateUserInfo(FirebaseUser user){
        if(user != null) {
            Intent intent = new Intent(MainActivity.this, UserPage.class);
            intent.putExtra("Username",getUserName(user.getEmail().toString()));
            intent.putExtra("FromWhere","SignIn");
            startActivity(intent);
        }
    }

    private String getUserName(String email){
        int indexAt = email.indexOf("@");
        String username = email.substring(0,indexAt);
        return username;
    }

    private void logInUser(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailField.getText().toString();
                final String password = passwordField.getText().toString();
                if(email.length() > 5 && password.length() > 5) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                updateUserInfo(user);
                            } else {
                                Toast.makeText(MainActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initiateLoginProcess(){
        if(firebaseAuth.getCurrentUser() != null) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            updateUserInfo(user);
        }
    }

    private void createAccount(){
        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateAccount.class);
                startActivity(intent);
            }
        });
    }

    private void assignUiID(){
        signInButton = (Button) findViewById(R.id.signInButton);
        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        createAccountText = (TextView) findViewById(R.id.createAccountText);
    }
}
