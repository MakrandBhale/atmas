package com.makarand.atmas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import Constants.Constants;
import Helper.LocalStorage;
import Model.StudentUser;
import Model.User;

public class MainActivity extends AppCompatActivity {
    LocalStorage localStorage;
    EditText emailEditText;
    EditText passwordEditText;
    Button signinButton;
    FirebaseAuth mAuth;
    DatabaseReference userDbRef;
    TextView registerHere;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localStorage = LocalStorage.getInstance(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signinButton = findViewById(R.id.signin_button);
        registerHere = findViewById(R.id.register_textview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        toolbar.setTitle(null);

        if(mAuth.getCurrentUser() != null){
                proceed(mAuth.getCurrentUser());
        }

        /*if(localStorage.exists(Constants.USER_TYPE)){
            // user type is selected by user, for now its just buttons
            // will add authentication based later
            if(localStorage.getInt(Constants.USER_TYPE) == Constants.TEACHER){
                // if user type is teacher
                if(localStorage.exists(Constants.TEACHER_SUBJECT_LIST)){
                    // user completed registration redirect to teacher activity,
                    startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
                } else {
                    // if usertype is selected but subject list is not present then
                    // user has not completed registration
                    startActivity(new Intent(getApplicationContext(), TeacherRegistration.class));
                }
                finish();
            } else {
                // user type is student

                if(localStorage.exists(Constants.STUDENT_INFO_KEY)){
                    // student has completed registration, go to student page
                    startActivity(new Intent(getApplicationContext(), StudentActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), StudentRegistration.class));
                }
                finish();
            }
        }*/

        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), StudentRegistration.class));
            }
        });
    }

    public void signin(View v){
        if(validate()){
            Toast.makeText(this, "All fields are necessary.", Toast.LENGTH_LONG).show();
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        disableUI();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        proceed(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        enableUI();
                    }
                });
    }

    private void proceed(FirebaseUser user) throws NullPointerException {
        disableUI();
            userDbRef = FirebaseDatabase.getInstance().getReference("user/"+user.getUid());
            userDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Gson gson = new Gson();
                    try{
                        if(user.getUserType().equals(Constants.USER_STUDENT)){
                            StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                            localStorage.writeString(Constants.USER_INFO_KEY, gson.toJson(studentUser));
                            startStudentActivity(null);
                        } else if(user.getUserType().equals(Constants.USER_TEACHER)){
                            localStorage.writeString(Constants.USER_INFO_KEY, gson.toJson(user));
                            startTeacherActivity(null);
                        }
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    enableUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    enableUI();
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private boolean validate(){
        return emailEditText.getText().toString().length() <= 3 || passwordEditText.getText().toString().length() <= 3;
    }

    public void startStudentActivity(View v){
        // user is new, redirect to registration
        localStorage.writeInt(Constants.USER_TYPE, Constants.STUDENT);
        startActivity(new Intent(getApplicationContext(), StudentActivity.class));
        finish();
    }

    public void startTeacherActivity(View v){
        // user is new, redirect to registration
        localStorage.writeInt(Constants.USER_TYPE, Constants.TEACHER);
        if(localStorage.getBoolean(Constants.REGISTRATION_DONE)){
            startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), TeacherRegistration.class));
            finish();
        }

    }

    private void disableUI(){
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        signinButton.setText("PLEASE WAIT...");
        registerHere.setEnabled(false);
    }

    private void enableUI(){
        emailEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        signinButton.setText("SIGN IN");
        registerHere.setEnabled(true);
    }

}



