package com.example.akash.fbla_library;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by akash on 1/27/2018.
 */

public class BookMarkRecyclerAdapter extends RecyclerView.Adapter<BookMarkRecyclerAdapter.ViewHolder> {

    private ArrayList<String> bookString;
    private ArrayList<String> authorString;
    private ArrayList<RecyclerItem> bookDueRecyclerArrayList;
    private Context context;
    private boolean isBookmark;

    BookMarkRecyclerAdapter(ArrayList<String> a, ArrayList<String> b, Context context, boolean isBookmark){
        bookString = a;
        authorString = b;
        this.context = context;
        this.isBookmark = isBookmark;
    }

    BookMarkRecyclerAdapter(ArrayList<RecyclerItem> bookDueRecyclerArrayList, boolean isBookmark){
        this.isBookmark = isBookmark;
        this.bookDueRecyclerArrayList = bookDueRecyclerArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (isBookmark) {
            holder.bookTextView.setText(bookString.get(position));
            holder.authorTextView.setText(authorString.get(position));
        } else {
            holder.bookTextView.setText(bookDueRecyclerArrayList.get(position).getHeading());
            holder.authorTextView.setText(bookDueRecyclerArrayList.get(position).getDescription());
        }
    }

    @Override
    public int getItemCount() {
        if(isBookmark){
            return authorString.size();
        } else {
            return bookDueRecyclerArrayList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView bookTextView;
        TextView authorTextView;
        ImageView deleteOptions;
        public ViewHolder(final View itemView) {
            super(itemView);
            bookTextView = itemView.findViewById(R.id.bookHeaderTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            deleteOptions = itemView.findViewById(R.id.deleteOptions);
            if (isBookmark) {
                deleteOptions.setVisibility(View.VISIBLE);
                deleteOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Bookmarks").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ArrayList<String> books = new ArrayList<>();
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    books.add(d.getKey());
                                    if (books.size() == dataSnapshot.getChildrenCount()) {
                                        for (int i = 0; i < books.size(); i++) {
                                            if (bookTextView.getText().toString().equals(books.get(i))) {
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Bookmarks").child(bookTextView.getText().toString()).removeValue();
                                                notifyItemRemoved(i);
                                                notifyDataSetChanged();
                                                Intent intent = new Intent(itemView.getContext(), BookMarkActivity.class);
                                                intent.putExtra("DontSendBack", "TRUE");
                                                itemView.getContext().startActivity(intent);
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
                });
            } else {
                deleteOptions.setVisibility(View.GONE);
            }
        }
    }
}

