package com.example.akash.fbla_library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FeedbackActivity extends AppCompatActivity {

    public static final String FIREBASE_HEADER = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"));
    private EditText feedbackEditText;
    private TextView sentByTextView;
    private Button sendFeedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedbackEditText = findViewById(R.id.feedbackEditText);
        sentByTextView = findViewById(R.id.sentFromTextView);
        sendFeedbackButton = findViewById(R.id.sendFeedbackButton);

        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("FullName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String name = dataSnapshot.getValue().toString();
                    sentByTextView.setText("Sent By: " + name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackEditText.getText().toString() != null || feedbackEditText.getText().toString().length() < 1) {
                    FirebaseDatabase.getInstance().getReference().child("Feedback").child(FIREBASE_HEADER).child("Feedback").setValue(feedbackEditText.getText().toString());
                    Intent i = new Intent(FeedbackActivity.this, UserPage.class);
                    startActivity(i);
                }
            }
        });

    }
}
