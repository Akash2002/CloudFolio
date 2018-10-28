package com.example.akash.fbla_library;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.IDNA;
import android.provider.ContactsContract;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccount extends MainActivity {

    private EditText password1;
    private EditText password2;
    private EditText fullName;
    private EditText schoolName;
    private EditText emailID;
    private Button createAccountButton;
    private ToggleButton userOrAdminToggle;
    private Spinner gradeSpinner;
    private FirebaseAuth firebaseAuth;
    private Boolean continueToCreateAccount = false;
    private String userType = "Student";
    private ArrayAdapter<CharSequence> spinnerArrayAdapter;
    private CharSequence gradeLevel;
    private TextView loginBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().setTitle("Create Account");
        firebaseAuth = FirebaseAuth.getInstance();
        loginBack = findViewById(R.id.loginBack);

        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateAccount.this, MainActivity.class));
            }
        });

        assignUIid();
        udpateSpinnerUI();
        createAccount();
    }

    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccount.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create();
        builder.show();
    }

    private void udpateSpinnerUI(){
        spinnerArrayAdapter = ArrayAdapter.createFromResource(CreateAccount.this, R.array.gradeLevel, android.R.layout.simple_list_item_1);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(spinnerArrayAdapter);

        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gradeLevel = spinnerArrayAdapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void assignUIid(){
        password1 = (EditText) findViewById(R.id.passwordField);
        password2 = (EditText) findViewById(R.id.passwordField2);
        fullName = (EditText) findViewById(R.id.fullName);
        schoolName = (EditText) findViewById(R.id.schoolName);
        emailID = (EditText) findViewById(R.id.emailInput);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);
        userOrAdminToggle = (ToggleButton) findViewById(R.id.teacherStudentToggle);
        gradeSpinner = (Spinner) findViewById(R.id.gradeSpinner);

        fullName.requestFocus();
    }

    private void createAccount(){
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //UserPage.firebaseCounter++;
                if(password1.getText().toString().equals("")){
                    showAlertDialog("Password Missing","Please enter your password.");
                    continueToCreateAccount = false;
                } else if(password2.getText().toString().equals("")){
                    showAlertDialog("Password Missing","Please enter your password");
                    continueToCreateAccount = false;
                } else if(!(password1.getText().toString().equals(password2.getText().toString()))){
                    showAlertDialog("Password Mismatch","Please re-check your passwords, they don't match");
                    continueToCreateAccount = false;
                } else if(fullName.getText().equals("")){
                    showAlertDialog("Name Missing","Please enter your full name.");
                    continueToCreateAccount = false;
                } else if(schoolName.getText().equals("")){
                    showAlertDialog("School Name Missing","Please enter your school name.");
                    continueToCreateAccount = false;
                } else if(emailID.getText().equals("")){
                    showAlertDialog("EmailID Missing","Please enter your emailID.");
                    continueToCreateAccount = false;
                } else if(password1.getText().toString().length() < 6){
                    showAlertDialog("Short Password","Password is too short. Please try again");
                } else {
                    continueToCreateAccount = true;
                }

                userOrAdminToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(!compoundButton.isChecked()){
                            userType = "User";
                        } else {
                            userType = "Admin";
                        }
                    }
                });

                if(continueToCreateAccount){
                    final String passwordString1 = password1.getText().toString();
                    final String passwordString2 = password2.getText().toString();
                    final String fullNameString = fullName.getText().toString();
                    final String schoolNameString = schoolName.getText().toString();
                    final String emailString = emailID.getText().toString();
                    final String gradeLevelString = (String) gradeLevel;
                    if (passwordString1.equals(passwordString2)) {
                        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"You have created an account successfully!",Toast.LENGTH_LONG).show();
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if(user != null) {
                                        Intent intent = new Intent(CreateAccount.this, UserPage.class);
                                        intent.putExtra("FromWhere","CreateAccount");
                                        intent.putExtra("FullName", fullNameString);
                                        intent.putExtra("School",schoolNameString);
                                        intent.putExtra("EmailID",emailString);
                                        intent.putExtra("UserType",userType);
                                        intent.putExtra("GradeLevel",gradeLevelString);
                                        intent.putExtra("Username", FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@")));
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
