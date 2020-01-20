package com.makarand.atmas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import Constants.Constants;
import Helper.LocalStorage;
import Model.StudentInfo;
import Model.StudentUser;

public class StudentRegistration extends AppCompatActivity {
    EditText grNoEditText, nameEditText, rollEditText, classEditText, emailEditText, passwordEditText;
    Button proceedButton;
    //StudentInfo studentInfo;
    StudentUser studentUser;
    LocalStorage localStorage;
    String name, grNo, studentClass, roll, email, password;
    Gson gson;
    FirebaseAuth mAuth;
    TextView goToLoginText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);
        grNoEditText = findViewById(R.id.gr_no);
        nameEditText = findViewById(R.id.name);
        proceedButton = findViewById(R.id.proceed_button);
        rollEditText  = findViewById(R.id.roll_number);
        classEditText = findViewById(R.id.student_class);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        goToLoginText = findViewById(R.id.go_to_login);
        localStorage  = LocalStorage.getInstance(this);

        gson = new Gson();
/*        if(localStorage.exists(Constants.STUDENT_INFO_KEY)){
            startActivity(new Intent(this, StudentActivity.class));
            finish();
        }*/
        mAuth = FirebaseAuth.getInstance();
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    disableUI();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("user/"+mAuth.getCurrentUser().getUid());
                                    studentUser = new StudentUser(name, email, mAuth.getCurrentUser().getUid(), Constants.USER_STUDENT, roll, studentClass, grNo);
                                    dbRef.setValue(studentUser);
                                    localStorage.writeString(Constants.STUDENT_INFO_KEY, gson.toJson(studentUser));
                                    startActivity(new Intent(StudentRegistration.this, StudentActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(StudentRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    enableUI();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter correct information.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private boolean validate() {
        // THis is very basic version for validation function.
        grNo = grNoEditText.getText().toString().trim().toUpperCase();
        name = nameEditText.getText().toString().trim();
        roll = rollEditText.getText().toString().trim();
        studentClass = classEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        return grNo.length() > 5 && name.length() > 3 && roll.length() > 1 && studentClass.length() > 3 && email.length() > 3 && password.length() > 3;
    }

    public void goToLogin(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void enableUI(){
        emailEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        grNoEditText.setEnabled(true);
        nameEditText.setEnabled(true);
        rollEditText.setEnabled(true);
        classEditText.setEnabled(true);
        proceedButton.setEnabled(true);
        proceedButton.setText("PROCEED");
        goToLoginText.setEnabled(true);
    }

    private void disableUI(){
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        grNoEditText.setEnabled(false);
        nameEditText.setEnabled(false);
        rollEditText.setEnabled(false);
        classEditText.setEnabled(false);
        proceedButton.setEnabled(false);
        proceedButton.setText("CREATING USER...");
        goToLoginText.setEnabled(false);
    }
}
