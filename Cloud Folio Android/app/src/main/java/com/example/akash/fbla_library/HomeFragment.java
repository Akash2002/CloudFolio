package com.example.akash.fbla_library;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    int counter = 0;
    static String FIREBASE_HEADER;
    final static double AMOUNT_PER_DAY = 0.25;
    static boolean setStock = false;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        UserPage.actionBar.setTitle("Home");

        final TextView bookofdayTextView = v.findViewById(R.id.bookofdaytextView);
        final ConstraintLayout randomBooklayout = v.findViewById(R.id.randomBookLayout);
        FIREBASE_HEADER = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"));
        final ArrayList<String> books = new ArrayList<>();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Books");

        v.findViewById(R.id.checkLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CheckoutActivity.class));
            }
        });

        v.findViewById(R.id.holdLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HoldActivity.class));
            }
        });

        v.findViewById(R.id.bookmarkLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BookMarkActivity.class));
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> books = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    books.add(d.getKey());
                    if(books.size() == dataSnapshot.getChildrenCount()){
                        int random = (int)(Math.random() * books.size());
                        String bookname = books.get(random);
                        bookofdayTextView.setText(bookname);
                        randomBooklayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                databaseReference.child(bookofdayTextView.getText().toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Map<String, String> data = (Map) dataSnapshot.getValue();
                                        if(data != null) {
                                            if (getActivity() != null) {
                                                if (getActivity().getPackageName() != null) {
                                                    Intent i = new Intent(getActivity(), BookAttributeActivity.class);
                                                    i.putExtra("BookName", bookofdayTextView.getText().toString());
                                                    i.putExtra("BookDescription", data.get("Description"));
                                                    i.putExtra("BookGenre", data.get("Genre"));
                                                    i.putExtra("BookLexile", data.get("Lexile"));
                                                    i.putExtra("BookPages", String.valueOf(data.get("Pages")));
                                                    i.putExtra("BookStock", String.valueOf(data.get("Stock")));
                                                    i.putExtra("BookAuthor", String.valueOf(data.get("Author")));
                                                    startActivity(i);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                }
                checkForArrival(books);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        checkForDueDate();
        checkForHold();
        checkForPickup();
        return v;
    }

    public static void checkForArrival(final ArrayList<String> books){
        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("Hold").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                books.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    books.add(d.getKey());
                    if(books.size() == dataSnapshot.getChildrenCount()) {
                        System.out.println(books);
                        for (int i = 0; i < books.size(); i++) {
                            final int j = i;
                            FirebaseDatabase.getInstance().getReference().child("Books").child(books.get(i)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Map<String, String> data = (Map) dataSnapshot.getValue();
                                    if (data != null) {
                                        Integer stock = Integer.parseInt(String.valueOf(data.get("Stock")));
                                        if (stock > 0) {
                                            if (j < books.size()) {
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("ReadyForPickup").child(books.get(j)).setValue(DateChecker.addDaysToDate(new Date(), 10));
                                                System.out.println("Book arrived " + books.get(j));
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

    public static void checkForDueDate(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, String> data = (Map) dataSnapshot.getValue();
                if(data != null) {
                    if (data.get("Checkout") != null) {
                        databaseReference.child("Checkout").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> books = new ArrayList<>();
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    books.add(dataSnapshot1.getKey());
                                    if (books.size() == dataSnapshot.getChildrenCount()) {
                                        for (final String bookName : books) {
                                            databaseReference.child("Checkout").child(bookName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Map<String, String> dates = (Map) dataSnapshot.getValue();
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                                                    try {
                                                        if (dates != null) {
                                                            if(dates.get("DateDue") != null) {
                                                                if (new Date().after(simpleDateFormat.parse(dates.get("DateDue")))) {
                                                                    String days = DateChecker.calculateDays(simpleDateFormat.parse(dates.get("DateDue")), new Date());
                                                                    databaseReference.child("Due").child(bookName).child("NumDays").setValue(days);
                                                                    databaseReference.child("Due").child(bookName).child("AmountDue").setValue(Integer.valueOf(days) * AMOUNT_PER_DAY);
                                                                }
                                                            }
                                                        }
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
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

    public static void checkForHold() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, String> data = (Map) dataSnapshot.getValue();
                if (data != null) {
                    if (data.get("Hold") != null) {
                        databaseReference.child("Hold").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> books = new ArrayList<>();
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    books.add(dataSnapshot1.getKey());
                                    if (books.size() == dataSnapshot.getChildrenCount()) {
                                        for (final String bookName : books) {
                                            databaseReference.child("Hold").child(bookName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Map<String, String> data = (Map) dataSnapshot.getValue();
                                                    if (data != null) {
                                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
                                                        try {
                                                            if (data.get("CheckoutLimit") != null) {
                                                                if (new Date().after(simpleDateFormat.parse(data.get("CheckoutLimit")))) {
                                                                    databaseReference.child("Hold").child(bookName).removeValue();
                                                                    databaseReference.child("ReadyForPickup").child(bookName).removeValue();
                                                                    FirebaseDatabase.getInstance().getReference().child("Books").child(bookName).child("Stock").addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                                            Integer stock = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                                                                            if (stock != null) {
                                                                                if (!setStock) {
                                                                                    FirebaseDatabase.getInstance().getReference().child("Books").child(bookName).child("Stock").setValue(stock + 1);
                                                                                    setStock = true;
                                                                                }
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
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

    public static void checkForPickup(){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, String> data = (Map) dataSnapshot.getValue();
                if (data != null) {
                    if (data.get("ReadyForPickup") != null) {
                        databaseReference.child("ReadyForPickup").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> books = new ArrayList<>();
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    books.add(dataSnapshot1.getKey());
                                    if (books.size() == dataSnapshot.getChildrenCount()) {
                                        for (final String bookName : books) {
                                            databaseReference.child("ReadyForPickup").child(bookName).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    String data = String.valueOf(dataSnapshot.getValue());
                                                    if (data != null) {
                                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                                        try {
                                                            if (new Date().after(simpleDateFormat.parse(data))) {
                                                                FirebaseDatabase.getInstance().getReference().child("HeldBooks").child(bookName).removeValue();
                                                                databaseReference.child("Hold").child(bookName).removeValue();
                                                                databaseReference.child("ReadyForPickup").child(bookName).removeValue();
                                                                FirebaseDatabase.getInstance().getReference().child("Books").child(bookName).child("Stock").addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        Integer stock = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                                                                        if (stock != null) {
                                                                            if (!setStock) {
                                                                                FirebaseDatabase.getInstance().getReference().child("Books").child(bookName).child("Stock").setValue(stock + 1);
                                                                                setStock = true;
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
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

    public void getBooksList(){
        FirebaseDatabase.getInstance().getReference().child("Books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> books = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    books.add(d.getKey());
                    if(books.size() == dataSnapshot.getChildrenCount()){
                        checkForArrival(books);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
