package com.example.akash.fbla_library;


import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserHoldFragment extends Fragment {

    private ArrayList<HoldDataManager> holdDataManagerArrayList;
    private RecyclerView holdsRecyclerView;
    private ArrayList<String> booksHeld;
    private ArrayList<String> dueDatesArrayList;
    public static String FIREBASE_HEADER;
    private ArrayList<String> checkoutLimitDateArrayList;
    public boolean passthrough = false;
    private ArrayList<RecyclerItem> holdRecyclerItemArrayList;

    public UserHoldFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_hold, container, false);
        FIREBASE_HEADER = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"));
        HoldActivity.actionBar.setTitle("Holds");
        dueDatesArrayList = new ArrayList<>();
        holdDataManagerArrayList = new ArrayList<HoldDataManager>();
        holdRecyclerItemArrayList = new ArrayList<>();
        booksHeld = new ArrayList<>();
        passthrough = true;

        holdsRecyclerView = v.findViewById(R.id.holdRecyclerView);

        FirebaseDatabase.getInstance().getReference().child("Users").child(this.FIREBASE_HEADER).child("Hold").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkoutLimitDateArrayList = new ArrayList<>();
                for (DataSnapshot books : dataSnapshot.getChildren()) {
                    if (books != null) {
                        booksHeld.add(String.valueOf(books.getKey()));
                        if (booksHeld.size() == dataSnapshot.getChildrenCount()) {
                            Log.i("Change", booksHeld.toString());
                            getDueDate(booksHeld);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }


    public void getDueDate(final ArrayList<String> booksCheckedOut){
        Log.i("Change", "Change");
        for(int i = 0; i < booksCheckedOut.size(); i++){
            FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Hold").child(booksCheckedOut.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> data = (Map) dataSnapshot.getValue();
                    if(data != null) {
                        checkoutLimitDateArrayList.add(data.get("HoldDate"));
                        if (checkoutLimitDateArrayList.size() == booksCheckedOut.size()) {
                            populateRecyclerView(booksCheckedOut, checkoutLimitDateArrayList);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void populateRecyclerView(ArrayList<String> booksCheckedOut, ArrayList<String> checkoutDatesArrayList){
        ArrayList<RecyclerItem> checkoutBookRecyclerArray = new ArrayList<>();
        for(int i = 0; i < booksCheckedOut.size(); i++){
            RecyclerItem item = new RecyclerItem(booksCheckedOut.get(i), "Held Until: " + checkoutDatesArrayList.get(i));
            checkoutBookRecyclerArray.add(item);
        }
        holdsRecyclerView.setHasFixedSize(false);
        holdsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        HoldCheckoutRecyclerAdapter holdCheckoutRecyclerAdapter = new HoldCheckoutRecyclerAdapter(getActivity(), checkoutBookRecyclerArray, checkoutDatesArrayList, false);
        holdsRecyclerView.setAdapter(holdCheckoutRecyclerAdapter);
    }


}

