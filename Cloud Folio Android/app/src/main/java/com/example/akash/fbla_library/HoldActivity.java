package com.example.akash.fbla_library;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class HoldActivity extends AppCompatActivity {

    private ConstraintLayout pickupConstraint, heldBooksConstraint;
    private TextView pickupTextView, holdTextView;
    private Fragment goToFragment;
    private Intent intent;
    public static ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hold);

        actionBar = getSupportActionBar();

        heldBooksConstraint = findViewById(R.id.heldBooksConstraintLayout);
        pickupConstraint = findViewById(R.id.pickUpConstraintLayout);
        pickupTextView = findViewById(R.id.pickupSegmentText);
        holdTextView = findViewById(R.id.holdSegmenttext);
        getFragmentManager().beginTransaction().replace(R.id.constraintContainer, new UserHoldFragment()).commit();
        pickupConstraint.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        heldBooksConstraint.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        pickupTextView.setTextColor(getColor(R.color.colorPrimaryDark));
        holdTextView.setTextColor(getColor(android.R.color.white));


        pickupConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickupConstraint.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                heldBooksConstraint.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                holdTextView.setTextColor(getColor(R.color.colorPrimaryDark));
                pickupTextView.setTextColor(getColor(android.R.color.white));
                goToFragment = new UserPickupFragment();
                getFragmentManager().beginTransaction().replace(R.id.constraintContainer, goToFragment).commit();
            }
        });

        heldBooksConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickupConstraint.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                heldBooksConstraint.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                pickupTextView.setTextColor(getColor(R.color.colorPrimaryDark));
                holdTextView.setTextColor(getColor(android.R.color.white));
                goToFragment = new UserHoldFragment();
                getFragmentManager().beginTransaction().replace(R.id.constraintContainer, goToFragment).commit();
            }
        });

        new HomeFragment().getBooksList();

    }
}
