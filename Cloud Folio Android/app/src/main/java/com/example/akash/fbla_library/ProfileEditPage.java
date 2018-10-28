package com.example.akash.fbla_library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileEditPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_page2);

        final EditText nameInput = findViewById(R.id.nameTextInput);
        final EditText schoolInput = findViewById(R.id.schoolText);
        final EditText gradeLevel = findViewById(R.id.gradeLevelText);
        final Button saveChanges = findViewById(R.id.saveChangesButton);
        final TextView nameTextView = findViewById(R.id.nameTextView);

        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("FullName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameTextView.setText(String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(nameInput.getText().toString().length() > 0 || schoolInput.getText().toString().length() > 0 || gradeLevel.getText().toString().length() > 0)){
                    Toast.makeText(getApplicationContext(), "Please enter something", Toast.LENGTH_SHORT).show();
                } else {
                    if(nameInput.getText().toString().length() > 0){
                        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("FullName").setValue(nameInput.getText().toString());
                    }
                    if(schoolInput.getText().toString().length() > 0){
                        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("School").setValue(schoolInput.getText().toString());
                    }
                    if(gradeLevel.getText().toString().length() > 0){
                        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("GradeLevel").setValue(gradeLevel.getText().toString());
                    }
                }
                Intent i = new Intent(ProfileEditPage.this, UserPage.class);
                i.putExtra("FromWhere", "ProfileEditPage");
                startActivity(i);
            }
        });

    }
}
