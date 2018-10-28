package com.example.akash.fbla_library;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class DuesActivity extends AppCompatActivity {

    RecyclerView booksDueRecyclerView;
    TextView totalAmountTextView;
    double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dues);

        booksDueRecyclerView = findViewById(R.id.dueBooksList);
        totalAmountTextView = findViewById(R.id.totalDueTextView);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, String> data = (Map) dataSnapshot.getValue();
                if (data != null) {
                    if (data.get("Due") != null) {
                        final ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
                        databaseReference.child("Due").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> books = new ArrayList<>();
                                for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                                    books.add(d1.getKey());
                                    if (books.size() == dataSnapshot.getChildrenCount()) {
                                        for (final String bookName : books) {
                                            databaseReference.child("Due").child(bookName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Map<String, String> data = (Map) dataSnapshot.getValue();
                                                    if (data != null) {
                                                        RecyclerItem item = new RecyclerItem(bookName, "Due: $ " + String.valueOf(data.get("AmountDue")));
                                                        Double amount = Double.parseDouble(String.valueOf(data.get("AmountDue")));
                                                        totalAmount += amount;
                                                        totalAmountTextView.setText(String.valueOf(totalAmount));
                                                        recyclerItems.add(item);
                                                        if (recyclerItems.size() == dataSnapshot.getChildrenCount()) {
                                                            BookMarkRecyclerAdapter bookMarkRecyclerAdapter = new BookMarkRecyclerAdapter(recyclerItems, false);
                                                            booksDueRecyclerView.setAdapter(bookMarkRecyclerAdapter);
                                                            booksDueRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
}
