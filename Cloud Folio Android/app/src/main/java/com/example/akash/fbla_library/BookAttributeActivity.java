package com.example.akash.fbla_library;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookAttributeActivity extends AppCompatActivity {

    private Button checkoutButton, holdButton;
    private String bookname, bookDescription, bookGenre, bookLexile, bookPages, bookStock, bookAuthor;
    private TextView bookNameTextView, bookDescriptionTextView, bookGenreTextView, bookLexileTextView, bookPageTextView, bookAuthorTextView, bookPublishDateTextView;
    private ArrayList<String> booksCheckedOut;
    private String currentDateCheckout;
    private boolean continueOn = false;
    private boolean previousCheckout = false;
    private ImageView bookmarkFrame;
    private boolean changeStock = false;
    boolean holdThere = false;
    private DatabaseReference databaseReference;
    private String FIREBASE_HEADER;
    private boolean datachange = false;
    private Button shareBookLayout;
    private boolean previousHold = false;
    public static boolean holdBook = true;String bookTextString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_attribute);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            FIREBASE_HEADER = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"));
            shareBookLayout = (Button) findViewById(R.id.shareBooklayout);

            bookNameTextView = findViewById(R.id.bookNameTextView);
            bookDescriptionTextView = findViewById(R.id.descriptionTextView);
            bookGenreTextView = findViewById(R.id.genreTextView);
            bookLexileTextView = findViewById(R.id.lexileTextView);
            bookPageTextView = findViewById(R.id.pagesTextView);
            bookAuthorTextView = findViewById(R.id.authorTextView);

            bookmarkFrame = findViewById(R.id.bookmarkButton);
            checkoutButton = findViewById(R.id.checkoutButton);
            holdButton = findViewById(R.id.holdButton);

            databaseReference = FirebaseDatabase.getInstance().getReference();
            booksCheckedOut = new ArrayList<>();

            showData();
            bookTextString = bookNameTextView.getText().toString();
            checkoutManager();
            holdManager();

            getSupportActionBar().setTitle(bookNameTextView.getText().toString());

            shareBookLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Read this book, " + bookTextString + ", from CloudFolio");
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Share " + bookTextString));
                }
            });

            bookmarkFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("Bookmarks").child(bookTextString).setValue(0);
                    Toast.makeText(getApplicationContext(), bookTextString + " added to your bookmark list", Toast.LENGTH_LONG).show();
                }
            });
            holdButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!previousCheckout) {
                        if(!previousHold) {
                            holdBook();
                        } else {
                            Toast.makeText(getApplicationContext(), "You have held out " + bookname + " already.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "You have checked out " + bookname + " already.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    public void holdManager() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> data = (Map) dataSnapshot.getValue();
                if(data != null) {
                    if (data.get("Hold") != null) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("Hold").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    if (!(data.getKey().equals(bookTextString))) {
                                        previousHold = false;
                                    } else {
                                        holdThere = true;
                                        previousHold = true;
                                        Toast.makeText(BookAttributeActivity.this, "You have already held " + bookTextString, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else if (data.get("Hold") == null) {
                        holdButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!previousCheckout)
                                    holdBook();
                                else
                                    Toast.makeText(getApplicationContext(), "You have checked out " + bookname + " already.", Toast.LENGTH_SHORT).show();
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
    public void holdBook(){
        System.out.println("Book Held "+ bookTextString);
        ArrayList<String> strings = new ArrayList<>();
        strings.add(bookTextString);
        System.out.println("Strings"+strings);
        bookTextString = strings.get(0);
        FirebaseDatabase.getInstance().getReference().child("Users").child(this.FIREBASE_HEADER).child("Hold").child(bookTextString).child("HoldDate").setValue(addDaysToDate(new Date(), 30));
        FirebaseDatabase.getInstance().getReference().child("Users").child(this.FIREBASE_HEADER).child("Hold").child(bookTextString).child("CheckoutLimit").setValue(addDaysToDate(new Date(), 7));
        FirebaseDatabase.getInstance().getReference().child("Books").child(bookTextString).child("Stock").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Integer value = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                    if (value > 0) {
                        FirebaseDatabase.getInstance().getReference().child("HeldBooks").child(bookTextString).child(FIREBASE_HEADER).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String num = String.valueOf(dataSnapshot.getValue());
                                if (num != null) {
                                    if (num.equals("1")) {
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(FIREBASE_HEADER).child("ReadyForPickup").child(bookTextString).setValue(addDaysToDate(new Date(), 10));
                                        FirebaseDatabase.getInstance().getReference().child("Books").child(bookTextString).child("Stock").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int value = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                                                if (value > 0) {
                                                    if (!changeStock) {
                                                        FirebaseDatabase.getInstance().getReference().child("Books").child(bookTextString).child("Stock").setValue(value - 1);
                                                        changeStock = true;
                                                        startActivity(new Intent(getApplicationContext(), HoldActivity.class));
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("HeldBooks").child(bookTextString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!datachange) {
                    databaseReference.child("HeldBooks").child(bookTextString).child(FIREBASE_HEADER).setValue(String.valueOf((dataSnapshot.getChildrenCount() + 1)));
                    System.out.println("Count" + dataSnapshot.getChildrenCount() + 1);
                    datachange = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void checkoutBook() throws ParseException {
        if (bookTextString != null && bookTextString.length() > 1) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Checkout").child(bookTextString)
                    .child("CheckoutDate").setValue(calculateCurrentDate());
            FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Checkout").child(bookTextString)
                    .child("DateDue").setValue(addDaysToDate(new Date(), 30));
            FirebaseDatabase.getInstance().getReference().child("Books").child(bookTextString).child("Stock").setValue(Integer.parseInt(bookStock) - 1);
        }
    }

    public void checkoutManager() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> data = (Map) dataSnapshot.getValue();
                if(data != null) {
                    String checkout = String.valueOf(data.get("Checkout"));
                    if (!(checkout.equals("null"))) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Checkout").addValueEventListener(new ValueEventListener() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot books : dataSnapshot.getChildren()) {
                                    if (books != null) {
                                        booksCheckedOut.add(String.valueOf(books.getKey()));
                                    }
                                    if (continueOn) {
                                        for (String checkedOutBookName : booksCheckedOut) {
                                            if (checkedOutBookName.equals(bookTextString)) {
                                                previousCheckout = true;
                                            }
                                        }
                                        if (Integer.parseInt(bookStock) > 0) {

                                            checkoutButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (!previousCheckout) {
                                                        if (!previousHold) {
                                                            try {
                                                                checkoutBook();
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                            Intent i = new Intent(BookAttributeActivity.this, CheckoutActivity.class);
                                                            startActivity(i);
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "You have held " + bookname + " already.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "You have checked out " + bookname + " already.", Toast.LENGTH_SHORT).show();
                                                    }
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

                    } else {
                        checkoutButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    checkoutBook();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Intent i = new Intent(BookAttributeActivity.this, UserPage.class);
                                i.putExtra("FromWhere", "BookAttributeActivity");
                                startActivity(i);
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

    void showData() {
        bookname = getIntent().getStringExtra("BookName");
        bookDescription = getIntent().getStringExtra("BookDescription");
        bookGenre = getIntent().getStringExtra("BookGenre");
        bookLexile = getIntent().getStringExtra("BookLexile");
        bookPages = getIntent().getStringExtra("BookPages");
        bookStock = getIntent().getStringExtra("BookStock");
        bookAuthor = getIntent().getStringExtra("BookAuthor");

        bookNameTextView.setText(bookname);
        bookDescriptionTextView.setText(bookDescription);
        bookGenreTextView.setText(bookGenre);
        bookLexileTextView.setText(bookLexile);
        bookPageTextView.setText(bookPages);
        bookAuthorTextView.setText(bookAuthor);

        continueOn = true;
    }

    public String calculateCurrentDate() {
        String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        String day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        currentDateCheckout = month + "-" + day + "-" + year;
        return currentDateCheckout;
    }

    public static String addDaysToDate(Date date, int numDays){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, numDays);
        return String.valueOf(simpleDateFormat.format(c.getTime()));
    }

}