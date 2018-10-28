package com.example.akash.fbla_library;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class BookFragment extends Fragment {

    private DatabaseReference databaseReference;
    int counter = 0;

    ArrayList<String> books = new ArrayList<>();
    ArrayList<Book> bookArrayList = new ArrayList<>();
    RecyclerView bookRecyclerView;

    public BookFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book, container, false);

        UserPage.actionBar.setTitle("Books");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Books");
        bookRecyclerView = v.findViewById(R.id.bookRecyclerView);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                books.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()){
                    books.add(String.valueOf(d.getKey()));
                    Log.i("Books",books.toString());
                    if(books.size() == (int) dataSnapshot.getChildrenCount()) {
                        getBookAttributes(books);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    public void getBookAttributes(final ArrayList<String> books){
        bookArrayList.clear();
        Log.i("Books", books.toString());
        for(String bookName: books){
            databaseReference.child(bookName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> data = (Map) dataSnapshot.getValue();
                    if (data != null) {
                        if (data != null) {
                            Book book = new Book(
                                    data.get("Name"),
                                    data.get("Author"),
                                    data.get("Description"),
                                    data.get("Genre"),
                                    data.get("Lexile"),
                                    String.valueOf(data.get("Pages")),
                                    String.valueOf(data.get("Stock")),
                                    data.get("Type")
                            );
                            bookArrayList.add(book);
                            populateRecyclerView(bookArrayList);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void populateRecyclerView(ArrayList<Book> bookArrayList){
        bookRecyclerView.setHasFixedSize(true);
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        BookDetailedListRecyclerAdapter bookDetailedListRecyclerAdapter = new BookDetailedListRecyclerAdapter(bookArrayList, getActivity());
        bookRecyclerView.setAdapter(bookDetailedListRecyclerAdapter);
    }

}
