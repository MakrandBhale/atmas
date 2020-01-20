package com.makarand.atmas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.makarand.atmas.Events.OnSeatGridReady;
import com.makarand.atmas.Events.OnSubjectListViewReady;
import com.makarand.atmas.Fragments.AttendanceFragment;
import com.makarand.atmas.Fragments.SubjectChoiceFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Constants.Constants;
import Helper.LocalStorage;
import Model.AttendanceReportTree;
import Model.BroadcastMessageFormat;
import Model.FeedbackMessageFormat;
import Model.StudentInfo;
import Model.Subject;

public class TeacherActivity extends AppCompatActivity implements
        SubjectChoiceFragment.OnFragmentInteractionListener,
        OnSubjectListViewReady,
        OnSeatGridReady,
        AttendanceFragment.OnFragmentInteractionListener
{
    Button sendButton, addSubjectButton;
    MessageListener mMessageListener;
    TextView success, error;
    FrameLayout fragmentFrame;
    int count = 0;
    String message;
    ArrayList<Subject> subjectArrayList = new ArrayList<>();
    LocalStorage localStorage;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;
    private Subject selectedSubject;
    String teacherName;
    Gson gson;
    MessageListener messageListener;
    Map<String, StudentInfo> studentInfoMap = new HashMap<>();
    AttendanceFragment attendanceFragment;
    FirebaseAuth mAuth;
    String uid, path, randomKey;
    BroadcastMessageFormat broadcastMessageFormat;
    DatabaseReference dbRef;
    Message mMessage;
    String messageString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this, MainActivity.class));
        }

        uid = mAuth.getUid();

        sendButton = findViewById(R.id.send_button);
        addSubjectButton = findViewById(R.id.add_subject_button);
        success = findViewById(R.id.success);
        error = findViewById(R.id.error);
        fragmentFrame = findViewById(R.id.fragment_frame);
        localStorage = LocalStorage.getInstance(this);
        teacherName = localStorage.getString(Constants.TEACHER_NAME_KEY);
        subjectArrayList = localStorage.getList(Constants.TEACHER_SUBJECT_LIST, new TypeToken<ArrayList<Subject>>(){}.getType());
        Log.d("boo", subjectArrayList.get(0).getSubjectName());
        gson = new Gson();
        SubjectChoiceFragment subjectChoiceFragment = new SubjectChoiceFragment();
        attendanceFragment  = new AttendanceFragment();
        path = "Attendance/"+uid+"/";
        randomKey = FirebaseDatabase.getInstance().getReference(path).push().getKey();
        dbRef = FirebaseDatabase.getInstance().getReference(path + randomKey);




        startFragment(subjectChoiceFragment, "Attendance Fragment");

        //messageListener = setupMessageListener();
        //startListening();
    }

    public void startFragment(Fragment fragment, String TAG){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_frame,fragment, TAG);
        fragmentTransaction.commit();
    }
    public String getTodaysDate(){
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    @Override
    public void sendBroadcastMessage() {
        //sendMessage(teacherName + ">" + selectedSubject.getSubjectName());
        AttendanceReportTree attendanceReportTree = new AttendanceReportTree(
                selectedSubject.getSubjectName(),
                getTodaysDate(),
                selectedSubject.getStrength()
        );

        dbRef.setValue(attendanceReportTree).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                messageString = gson.toJson(broadcastMessageFormat);
                sendMessage(messageString);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeacherActivity.this, "Failed to connect to database. Try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private PublishOptions getPublishOptions(){
        return new PublishOptions
                .Builder()
                .setStrategy(Constants.broadcastStrategy)
                .setCallback(new PublishCallback(){
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        error.setText("Message expired");
                    }
                })
                .build();
    }

    private void sendMessage(String msg){
        mMessage = new Message((msg).getBytes());
        Nearby.getMessagesClient(TeacherActivity.this).publish(mMessage, getPublishOptions())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        success.setText("Message Sent");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setText(e.getMessage());
                    }
                });
    }

    /*public SubscribeOptions getSubscribeOptions(){
        return new SubscribeOptions
                .Builder()
                .setStrategy(
                        Constants.scanStrategy
                )
                .setCallback(new SubscribeCallback(){
                    @Override
                    public void onExpired() {
                        super.onExpired();
                    }
                })
                .build();
    }

    private void startListening() {
        Nearby.getMessagesClient(TeacherActivity.this).subscribe(messageListener, getSubscribeOptions())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TeacherActivity.this, "Listening Messages.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherActivity.this, "Failed to listen to messages.", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

/*
    private MessageListener setupMessageListener() {
        return new MessageListener() {
            @Override
            public void onFound(final Message message) {
                Log.d("Boo", "Found message: " + new String(message.getContent()));
                final String receivedString = new String(message.getContent());
                //Toast.makeText(TeacherActivity.this, receivedString, Toast.LENGTH_SHORT).show();

                try{
                    StudentInfo studentInfo = gson.fromJson(receivedString, StudentInfo.class);
                    */
/*checking if the attendance belongs to our subject*//*


                    if(selectedSubject.getSubjectName().equals(studentInfo.getSubject())){
                        */
/*student belongs to selected subject and in the same class.
                        * Now check if the student has already sent report.
                        * *//*

                        if(!studentInfoMap.containsKey(studentInfo.getRoll())){
                            studentInfoMap.put(studentInfo.getRoll(), studentInfo);
                            attendanceFragment.updateAttendance(studentInfoMap);
                            */
/*Broadcasting message that the attendance is received.*//*

                            sendMessage(gson.toJson(new FeedbackMessageFormat(selectedSubject.getSubjectName(), studentInfo.getRoll())));
                        }
                    }

                } catch (JsonSyntaxException e){
                    e.printStackTrace();
                }
*/
/*
                Nearby.getMessagesClient(TeacherActivity.this).unsubscribe(messageListener)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startListening();
                            }
                        });*//*

            }

            @Override
            public void onLost(Message message) {
                Toast.makeText(TeacherActivity.this, "Message expired", Toast.LENGTH_SHORT).show();
                Log.d("Boo", "Lost sight of message: " + new String(message.getContent()));
            }
        };
    }
*/


    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    public ArrayList<Subject> getSubjectObject(){
        return subjectArrayList;
    }

    @Override
    public void onSubjectSelected(Subject subject) {
        //Toast.makeText(this, "Showing from Teacher Actvity: " + subject.getSubjectName(), Toast.LENGTH_SHORT).show();
        selectedSubject = subject;
        broadcastMessageFormat = new BroadcastMessageFormat(
                "AttendanceRequest",
                teacherName,
                selectedSubject.getSubjectName(),
                uid,
                randomKey
        );
        startFragment(attendanceFragment, "AttendanceFragment");
    }

/*    @Override
    public ArrayList<Subject> getSubjectListArray() {
        return subjectArrayList;
    }*/

    @Override
    public Subject getSelectedSubject() {
        return selectedSubject;
    }


    @Override
    public String getDatabaseToken() {
        return broadcastMessageFormat.getDatabaseToken();
    }

    @Override
    protected void onStart() {
        if(messageString != null)
            sendMessage(messageString);
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(mMessage != null)
            Nearby.getMessagesClient(this).unpublish(mMessage);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
