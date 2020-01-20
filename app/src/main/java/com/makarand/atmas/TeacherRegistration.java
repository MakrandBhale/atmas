package com.makarand.atmas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

import Constants.Constants;
import Helper.LocalStorage;
import Model.Subject;
import Model.TeacherUser;
import Model.User;

public class TeacherRegistration extends AppCompatActivity {
    EditText nameEditText, subjectNameEditText, strengthEditText, emailEditText;
    Button proceedButton;
    LocalStorage localStorage;
    String name, email;
    ArrayList<Subject> subjectListArray = new ArrayList<>();
    LinearLayout placeHolder;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);
        proceedButton = findViewById(R.id.proceed_button);
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        placeHolder = findViewById(R.id.place_holder);
        subjectNameEditText = findViewById(R.id.add_subject_editText);
        strengthEditText = findViewById(R.id.strength_editText);
        mAuth = FirebaseAuth.getInstance();
        final String uid = mAuth.getUid();
        final DatabaseReference userDbRef = FirebaseDatabase.getInstance().getReference("user/"+uid);
        dbRef = FirebaseDatabase.getInstance().getReference("user/"+uid+"/subjectList");
        localStorage = LocalStorage.getInstance(this);
        localStorage.setBoolean(Constants.REGISTRATION_DONE, false);


        proceedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(validate()){
                    localStorage.writeString(Constants.TEACHER_NAME_KEY, name);
                    localStorage.writeList(Constants.TEACHER_SUBJECT_LIST, subjectListArray);

                    userDbRef.setValue(new User(name, email, uid, Constants.USER_TEACHER))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    localStorage.setBoolean(Constants.REGISTRATION_DONE, true);
                                    for(final Subject subject : subjectListArray){
                                        dbRef.push().setValue(subject)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        localStorage.setBoolean(Constants.REGISTRATION_DONE, false);
                                                        Toast.makeText(TeacherRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        /*Subject is added to database no need to keep it in array.*/
                                                        subjectListArray.remove(subject);
                                                    }
                                                });
                                        if(!localStorage.getBoolean(Constants.REGISTRATION_DONE)){/*The subjects were not sent to database, atleast not all of them.*/
                                            break;
                                        }
                                    }
                                    if(localStorage.getBoolean(Constants.REGISTRATION_DONE))
                                        startActivity(new Intent(getApplicationContext(), TeacherActivity.class));
                                    else
                                        Toast.makeText(TeacherRegistration.this, "Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });


                } else {
                    Toast.makeText(TeacherRegistration.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void addSubjectList(View v){
        String subjectName = subjectNameEditText.getText().toString().trim();
        String strength = strengthEditText.getText().toString().trim();
        if(subjectName.length() <= 0){
            Toast.makeText(this, "Please enter subject name", Toast.LENGTH_SHORT).show();
            return;
        }
        if(strength.length() <= 0){
            Toast.makeText(this, "Please enter number of students", Toast.LENGTH_SHORT).show();
            return;
        }
        Subject subject = new Subject(subjectName,strength);
        subjectListArray.add(subject);
        createTextView();
        subjectNameEditText.setText("");
        strengthEditText.setText("");
        subjectNameEditText.requestFocus();
    }

    private void createTextView() {
        Subject subject = subjectListArray.get(subjectListArray.size()-1);
        TextView tv = new TextView(TeacherRegistration.this);
        tv.setText(subject.getSubjectName() + ", " + subject.getStrength() + " students");
        placeHolder.addView(tv);
    }

    private boolean validate() {
        // Very basic validation, need improvements.
        name = nameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        return name.length() > 3 && subjectListArray.size() > 0 && email.length() > 3;
    }



}
