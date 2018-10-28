package com.example.akash.fbla_library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by akash on 12/28/2017.
 */

public class HoldCheckoutRecyclerAdapter extends RecyclerView.Adapter<HoldCheckoutRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RecyclerItem> recyclerItems;
    private ArrayList<String> checkoutDateArrayList;
    private ArrayList<RecyclerItem> pickupArrayList;
    private boolean isCheckout;
    private boolean incrementStock = false;
    private boolean isPickup = false;

    public HoldCheckoutRecyclerAdapter(Context context,ArrayList<RecyclerItem> arrayList, int pickup){
        isPickup = pickup == 0;
        this.context = context;
        recyclerItems = arrayList;
    }

    public HoldCheckoutRecyclerAdapter(Context context, ArrayList<RecyclerItem> recyclerItems, boolean isCheckout) {
        this.context = context;
        this.recyclerItems = recyclerItems;
        this.isCheckout = isCheckout;
    }
    public HoldCheckoutRecyclerAdapter(Context context, ArrayList<RecyclerItem> recyclerItems, ArrayList<String> checkoutDateArrayList, boolean isCheckout) {
        this.context = context;
        this.recyclerItems = recyclerItems;
        this.checkoutDateArrayList = checkoutDateArrayList;
        this.isCheckout = isCheckout;
    }

    public HoldCheckoutRecyclerAdapter(Context context, ArrayList<RecyclerItem> recyclerItems) {
        this.context = context;
        this.recyclerItems = recyclerItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.hold_checkout_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecyclerItem recyclerItem = recyclerItems.get(position);
        holder.headerTextView.setText(recyclerItem.getHeading());
        holder.dueDateTextView.setText(recyclerItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView headerTextView, dueDateTextView;
        public ImageView showQueueOptions;

        public ViewHolder(final View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.booknameHeading);
            dueDateTextView = itemView.findViewById(R.id.dateDue);
            showQueueOptions = itemView.findViewById(R.id.options);

            if(isPickup){
                showQueueOptions.setVisibility(View.VISIBLE);
                showQueueOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BookAttributeActivity.holdBook = false;
                        BookDetailedListRecyclerAdapter.bool = false;
                        View sheetView = LayoutInflater.from(context).inflate(R.layout.pickup_bottom_sheet, null);
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                        bottomSheetDialog.setContentView(sheetView);
                        bottomSheetDialog.show();
                        ConstraintLayout constraintLayout = sheetView.findViewById(R.id.pickupLayoutButton);
                        constraintLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("ReadyForPickup").child(headerTextView.getText().toString()).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("HeldBooks").child(headerTextView.getText().toString()).child(ProfileFragment.FIREBASE_HEADER).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Hold").child(headerTextView.getText().toString()).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Checkout").child(headerTextView.getText().toString()).child("CheckoutDate").setValue(new SimpleDateFormat("MM-dd-yyyy").format(new Date()));
                                FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Checkout").child(headerTextView.getText().toString()).child("DateDue").setValue(new BookAttributeActivity().addDaysToDate(new Date(), 10));
                                Intent intent = new Intent(itemView.getContext(), UserPage.class);
                                intent.putExtra("FromWhere", "Pickup");
                                itemView.getContext().startActivity(intent);
                            }
                        });
                    }
                });
            }

            if(!isCheckout && !isPickup){
                showQueueOptions.setVisibility(View.VISIBLE);
                showQueueOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popup = new PopupMenu(itemView.getContext(), showQueueOptions);
                        popup.getMenuInflater().inflate(R.menu.options_popup_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if (menuItem.getItemId() == R.id.queueoptions) {
                                    Intent intent = new Intent(itemView.getContext(), BookHoldQueueActivity.class);
                                    intent.putExtra("BOOKNAME", headerTextView.getText().toString());
                                    itemView.getContext().startActivity(intent);
                                }
                                return true;
                            }
                        });
                        popup.show();
                    }
                });
            }

            if (isCheckout && !isPickup) {
                showQueueOptions.setVisibility(View.GONE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View v = LayoutInflater.from(context).inflate(R.layout.checkout_alert_layout, null);
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);

                        final TextView bookNameAlertHeader = v.findViewById(R.id.bookNameAlertTitle);
                        TextView dueDateAlertText = v.findViewById(R.id.dueDateAlertText);
                        final TextView checkoutDateAlertText = v.findViewById(R.id.checkoutDateAlertText);
                        final Button returnButton = v.findViewById(R.id.returnButtonAlert);

                        bookNameAlertHeader.setText(headerTextView.getText());
                        dueDateAlertText.setText("Due: " + dueDateTextView.getText());

                        for (int i = 0; i < recyclerItems.size(); i++) {
                            if (recyclerItems.get(i).getHeading().equals(bookNameAlertHeader.getText())) {
                                checkoutDateAlertText.setText("Checkout on: " + checkoutDateArrayList.get(i));
                            }
                        }

                        returnButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Checkout").child(bookNameAlertHeader.getText().toString()).child("CheckoutDate").removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Users").child(ProfileFragment.FIREBASE_HEADER).child("Checkout").child(bookNameAlertHeader.getText().toString()).child("DateDue").removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Books").child(bookNameAlertHeader.getText().toString()).child("Stock").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!incrementStock) {
                                            Integer stock = Integer.parseInt(dataSnapshot.getValue().toString());
                                            FirebaseDatabase.getInstance().getReference().child("Books").child(bookNameAlertHeader.getText().toString()).child("Stock").setValue((Integer) (stock + 1));
                                        }
                                        incrementStock = true;
                                        Intent i = new Intent(itemView.getContext(), UserPage.class);
                                        i.putExtra("FromWhere", "RecyclerAdapter");
                                        itemView.getContext().startActivity(i);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        v.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //dismiss
                            }
                        });

                        alert.setView(v);
                        alert.create().show();
                    }
                });

            }
        }
    }
}
