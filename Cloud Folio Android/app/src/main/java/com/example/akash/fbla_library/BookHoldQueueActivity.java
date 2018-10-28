package com.example.akash.fbla_library;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookHoldQueueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_hold_queue);

        getSupportActionBar().setTitle("Book Queue");

        TextView bookname = findViewById(R.id.booknameText);
        bookname.setText(getIntent().getStringExtra("BOOKNAME"));

        FirebaseDatabase.getInstance().getReference().child("HeldBooks").child(getIntent().getStringExtra("BOOKNAME")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> queue = new ArrayList<>();
                ArrayList<String> reverseQueue = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    queue.add(d.getKey());
                }
                for(int i = queue.size()-1; i >= 0; i--){
                    reverseQueue.add(queue.get(i));
                }
                System.out.println("Reverse QUeue" + reverseQueue);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, reverseQueue);
                ListView listView = findViewById(R.id.listView);
                listView.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
