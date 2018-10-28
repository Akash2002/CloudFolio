package com.example.akash.fbla_library;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public static String FIREBASE_HEADER = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"));

    ConstraintLayout checkoutView, holdView, bookmarkView, editProfileLayoutButton,dueAndFinesView;
    TextView numOfCheckoutsTextView, numofHoldsTextView, numOfBookmarksTextView, emailTextView;
    private ConstraintLayout logoutLayout;
    private ConstraintLayout feedbackLayout;
    double totalAmount;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        FIREBASE_HEADER = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"));
        UserPage.actionBar.setTitle("Profile");
        final TextView profileTextView = (TextView)v.findViewById(R.id.userFullNameText);
        final TextView userTypeTextView = (TextView)v.findViewById(R.id.userTypeTextView);
        dueAndFinesView = v.findViewById(R.id.dueAndFinesView);
        final TextView pickupText = v.findViewById(R.id.pickuptext);
        pickupText.setVisibility(View.INVISIBLE);
        emailTextView = v.findViewById(R.id.emailTextView);
        editProfileLayoutButton = v.findViewById(R.id.editProfileLayoutButton);
        logoutLayout = v.findViewById(R.id.logoutLayout);
        feedbackLayout = v.findViewById(R.id.feedbackLayout);
        final TextView amountTextView = v.findViewById(R.id.amountDue);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, String> data = (Map) dataSnapshot.getValue();
                if(data != null) {
                    if (data.get("Due") != null) {
                        final ArrayList<RecyclerItem> recyclerItems = new ArrayList<>();
                        databaseReference.child("Due").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                totalAmount = 0;
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
                                                        Double amount = Double.parseDouble(String.valueOf(data.get("AmountDue")));
                                                        totalAmount += amount;
                                                        amountTextView.setText("$" + String.valueOf(totalAmount));
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

        dueAndFinesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DuesActivity.class));
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(i);
            }
        });
        emailTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> data = (Map) dataSnapshot.getValue();
                if (data != null) {
                    profileTextView.setText(data.get("FullName"));
                    userTypeTextView.setText(data.get("UserType"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editProfileLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProfileEditPage.class));
            }
        });

        setHasOptionsMenu(true);
        bookmarkView = v.findViewById(R.id.bookmarkView);
        holdView = v.findViewById(R.id.holdView);
        checkoutView = v.findViewById(R.id.checkoutView);
        numOfCheckoutsTextView = v.findViewById(R.id.numofCheckoutsTextView);
        numofHoldsTextView = v.findViewById(R.id.numOfHoldsTextView);
        numOfBookmarksTextView = v.findViewById(R.id.numOfBookmarksTextView);
        //updateHeldBooks();
        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("Checkout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    numOfCheckoutsTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    numofHoldsTextView.setText("-");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("ReadyForPickup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if(dataSnapshot.getChildrenCount() > 0) {
                        pickupText.setVisibility(View.VISIBLE);
                        pickupText.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " Ready for Pickup!");
                    }
                } else {
                    numofHoldsTextView.setText("-");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("Hold").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    numofHoldsTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    numofHoldsTextView.setText("-");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("Bookmarks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    numOfBookmarksTextView.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                } else {
                    numOfBookmarksTextView.setText("-");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        checkoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CheckoutActivity.class));
            }
        });

        holdView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HoldActivity.class));
            }
        });

        bookmarkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BookMarkActivity.class));
            }
        });

        return v;
    }

    public void updateHeldBooks(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("HeldBooks");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> books = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    books.add(d.getKey());
                    if(books.size() == dataSnapshot.getChildrenCount()){
                        for(final String bookName: books){
                            System.out.println("Books"+books);
                            databaseReference.child(bookName).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final DataSnapshot numDataShot = dataSnapshot;
                                    ArrayList<String> users = new ArrayList<>();
                                    for(DataSnapshot d: dataSnapshot.getChildren()){
                                        users.add(d.getKey());
                                        if(users.size() == dataSnapshot.getChildrenCount()){
                                            for(final String name: users){
                                                System.out.println("Names" + name);
                                                if(name.equals(FIREBASE_HEADER)){
                                                    databaseReference.child(bookName).child(name).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            final DataSnapshot numShot = numDataShot;
                                                            System.out.println(dataSnapshot.getChildrenCount());
                                                            databaseReference.child(bookName).child(name).setValue(numShot.getChildrenCount());
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
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
