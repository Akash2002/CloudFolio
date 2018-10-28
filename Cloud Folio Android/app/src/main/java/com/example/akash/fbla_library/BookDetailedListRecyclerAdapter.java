package com.example.akash.fbla_library;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by akash on 12/28/2017.
 */

public class BookDetailedListRecyclerAdapter extends RecyclerView.Adapter<BookDetailedListRecyclerAdapter.ViewHolder> {

    private ArrayList<Book> bookArrayList;
    private Context context;
    boolean previousCheckout = false;
    static boolean bool = true;

    public BookDetailedListRecyclerAdapter(ArrayList<Book> bookArrayList, Context context){
        this.bookArrayList = bookArrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_recycler_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book = bookArrayList.get(position);
        holder.bookNameHeaderTextView.setText(book.getName());
        holder.authorSubHeaderTextView.setText(book.getAuthor());
        holder.genreSideTextView.setText(book.getGenre() + " " + book.getType());
        holder.stockSideTextView.setText(book.getStock());
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView bookNameHeaderTextView, authorSubHeaderTextView, genreSideTextView, stockSideTextView;

        public ViewHolder(final View itemView) {
            super(itemView);
            bookNameHeaderTextView = itemView.findViewById(R.id.bookNameHeaderTextView);
            authorSubHeaderTextView = itemView.findViewById(R.id.authorTextView);
            genreSideTextView = itemView.findViewById(R.id.genreTextView);
            stockSideTextView = itemView.findViewById(R.id.stockTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < bookArrayList.size(); i++) {
                        if (bookNameHeaderTextView.getText().toString().equals(bookArrayList.get(i).getName())) {
                            Intent intent = new Intent(itemView.getContext(), BookAttributeActivity.class);
                            intent.putExtra("BookName", bookArrayList.get(i).getName());
                            intent.putExtra("BookDescription", bookArrayList.get(i).getDescription());
                            intent.putExtra("BookGenre", bookArrayList.get(i).getGenre());
                            intent.putExtra("BookLexile", bookArrayList.get(i).getLexile());
                            intent.putExtra("BookPages", bookArrayList.get(i).getPages());
                            intent.putExtra("BookStock", bookArrayList.get(i).getStock());
                            intent.putExtra("BookType", bookArrayList.get(i).getType());
                            intent.putExtra("BookAuthor", bookArrayList.get(i).getAuthor());
                            intent.putExtra("Bool", "True");
                            itemView.getContext().startActivity(intent);
                        }
                    }
                }
            });

        }
    }
}
