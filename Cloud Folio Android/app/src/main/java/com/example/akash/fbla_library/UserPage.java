package com.example.akash.fbla_library;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;

public class UserPage extends AppCompatActivity {

    private TextView mTextMessage;
    private Fragment goToFragment;
    public static ActionBar actionBar;
    static BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        actionBar = getSupportActionBar();

        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        goToFragment = new HomeFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, goToFragment).commit();
        removeShiftMode(navigation);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"))).child("ReadyForPickup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayBadge(Math.round(Math.round(dataSnapshot.getChildrenCount())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        goToFragment = new HomeFragment();
                        break;
                    case R.id.navigation_books:
                        goToFragment = new BookFragment();
                        break;
                    case R.id.navigation_maps:
                        goToFragment = new MapFragment();
                        break;
                    case R.id.navigation_profile:
                        goToFragment = new ProfileFragment();
                        break;
                }
                getFragmentManager().beginTransaction().replace(R.id.container, goToFragment).commit();
                return true;
            }
        });

        if(getIntent().getStringExtra("FromWhere") != null) {
            if (getIntent().getStringExtra("FromWhere").equals("CreateAccount")) {
                DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                userDatabaseReference.child(getIntent().getStringExtra("Username"))
                        .child("FullName").setValue(getIntent().getStringExtra("FullName"));
                userDatabaseReference.child(getIntent().getStringExtra("Username"))
                        .child("School").setValue(getIntent().getStringExtra("School"));
                userDatabaseReference.child(getIntent().getStringExtra("Username"))
                        .child("EmailID").setValue(getIntent().getStringExtra("EmailID"));
                userDatabaseReference.child(getIntent().getStringExtra("Username"))
                        .child("UserType").setValue(getIntent().getStringExtra("UserType"));
                userDatabaseReference.child(getIntent().getStringExtra("Username"))
                        .child("GradeLevel").setValue(getIntent().getStringExtra("GradeLevel"));
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }

    public void displayBadge (int num) {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) UserPage.navigation.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(3);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        View badge = LayoutInflater.from(getApplicationContext()).inflate(R.layout.badge_layout, bottomNavigationMenuView, false);
        TextView t = badge.findViewById(R.id.notificationsbadge);
        if(num > 10) t.setText("10+");
        else t.setText(num+"");
        itemView.addView(badge);
    }

}
