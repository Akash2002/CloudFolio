package com.example.akash.fbla_library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class BookMarkActivity extends AppCompatActivity {

    boolean sendBack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);

        final RecyclerView bookmarkRecyclerView = findViewById(R.id.bookmarkRecyclerView);
        final ArrayList<String> authorList = new ArrayList<>();
        final ArrayList<String> books = new ArrayList<>();

        getSupportActionBar().setTitle("Bookmarks");

        if(getIntent().getStringExtra("DontSendBack") != null){
            if(getIntent().getStringExtra("DontSendBack").equals("TRUE")){
                sendBack = false;
            }
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> data = (Map) dataSnapshot.getValue();
                if (data != null) {
                    Log.i("Test", data.toString());
                    if (data.get("Bookmarks") != null) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Bookmarks").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    books.add(d.getKey());
                                    if (books.size() == dataSnapshot.getChildrenCount()) {
                                        for (int i = 0; i < books.size(); i++) {
                                            FirebaseDatabase.getInstance().getReference().child("Books").child(books.get(i)).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Map<String, String> data = (Map) dataSnapshot.getValue();
                                                    if (data != null) {
                                                        if (data.get("Author") != null) {
                                                            authorList.add(data.get("Author"));
                                                            if (authorList.size() == books.size()) {
                                                                BookMarkRecyclerAdapter bookmarkAdapter = new BookMarkRecyclerAdapter(books, authorList, getApplicationContext(), true);
                                                                bookmarkRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                bookmarkRecyclerView.setAdapter(bookmarkAdapter);
                                                            }
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BookMarkActivity.this, UserPage.class));
    }
}
