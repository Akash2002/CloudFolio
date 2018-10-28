package com.example.akash.fbla_library;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class UserPickupFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final String firebaseHeader = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"));
        View v = inflater.inflate(R.layout.fragment_user_pickup, container, false);
        final ArrayList<RecyclerItem> books = new ArrayList<>();
        final RecyclerView recyclerView = v.findViewById(R.id.pickupRecyclerView);
        final ArrayList<String> stringBookNames = new ArrayList<>();
        final ArrayList<String> bookPickupDate = new ArrayList<>();

        HoldActivity.actionBar.setTitle("Pickup");

        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseHeader).child("ReadyForPickup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    stringBookNames.add(d.getKey());
                    if(stringBookNames.size() == dataSnapshot.getChildrenCount()){
                        int i = 0;
                        for(String book: stringBookNames){
                            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseHeader).child("ReadyForPickup").child(stringBookNames.get(i)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null) {
                                        bookPickupDate.add(String.valueOf(dataSnapshot.getValue()));
                                        if (bookPickupDate.size() == stringBookNames.size()) {
                                            for (int i = 0; i < bookPickupDate.size(); i++) {
                                                RecyclerItem recyclerItem = new RecyclerItem(stringBookNames.get(i), "Pickup Before: " + bookPickupDate.get(i));
                                                books.add(recyclerItem);
                                                if (books.size() == bookPickupDate.size()) {
                                                    HoldCheckoutRecyclerAdapter holdCheckoutRecyclerAdapter = new HoldCheckoutRecyclerAdapter(getActivity(), books, 0);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                    recyclerView.setAdapter(holdCheckoutRecyclerAdapter);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            i++;
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

}
