package com.example.akash.fbla_library;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private ArrayList<HoldDataManager> holdDataManagerArrayList;
    private RecyclerView checkoutRecyclerView;
    private Context context;
    private ArrayList<String> booksCheckedOut;
    private ArrayList<String> dueDatesArrayList;
    public static String FIREBASE_HEADER;
    private ArrayList<String> checkoutDatesArrayList;
    public boolean passthrough = false;
    private TextView numOfCheckouts;
    private ArrayList<RecyclerItem> holdRecyclerItemArrayList;
    private RecyclerView holdRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        FIREBASE_HEADER = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"));

        getSupportActionBar().setTitle("Checkouts");

        booksCheckedOut = new ArrayList<>();
        checkoutDatesArrayList = new ArrayList<>();
        checkoutRecyclerView = findViewById(R.id.checkOutRecyclerView);
        dueDatesArrayList = new ArrayList<>();
        holdDataManagerArrayList = new ArrayList<HoldDataManager>();

        holdRecyclerItemArrayList = new ArrayList<>();

        passthrough = true;

        FirebaseDatabase.getInstance().getReference().child("Users").child(this.FIREBASE_HEADER).child("Checkout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot books: dataSnapshot.getChildren()){
                    if(books!=null) {
                        booksCheckedOut.add(String.valueOf(books.getKey()));
                        if (booksCheckedOut.size() == dataSnapshot.getChildrenCount()) {
                            getDueDate(booksCheckedOut);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getDueDate(final ArrayList<String> booksCheckedOut){
        for(int i = 0; i < booksCheckedOut.size(); i++){
            FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Checkout").child(booksCheckedOut.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> data = (Map) dataSnapshot.getValue();
                    if(data != null) {
                        dueDatesArrayList.add(data.get("DateDue"));
                        checkoutDatesArrayList.add(data.get("CheckoutDate"));
                        if (dueDatesArrayList.size() == booksCheckedOut.size() && checkoutDatesArrayList.size() == dueDatesArrayList.size()) {
                            populateRecyclerView(booksCheckedOut, dueDatesArrayList, checkoutDatesArrayList);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void populateRecyclerView(ArrayList<String> booksCheckedOut, ArrayList<String> dueDatesArrayList, ArrayList<String> checkoutDatesArrayList){
        ArrayList<RecyclerItem> checkoutBookRecyclerArray = new ArrayList<>();
        for(int i = 0; i < booksCheckedOut.size(); i++){
            RecyclerItem item = new RecyclerItem(booksCheckedOut.get(i), dueDatesArrayList.get(i));
            checkoutBookRecyclerArray.add(item);
        }
        checkoutRecyclerView.setHasFixedSize(false);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        HoldCheckoutRecyclerAdapter holdCheckoutRecyclerAdapter = new HoldCheckoutRecyclerAdapter(CheckoutActivity.this, checkoutBookRecyclerArray, checkoutDatesArrayList, true);
        checkoutRecyclerView.setAdapter(holdCheckoutRecyclerAdapter);
    }
}
