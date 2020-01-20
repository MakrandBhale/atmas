package com.makarand.atmas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.skyfishjy.library.RippleBackground;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import Constants.Constants;
import Helper.LocalStorage;
import Model.BroadcastMessageFormat;
import Model.StudentUser;

public class StudentActivity extends AppCompatActivity {
    MessageListener subjectListListener;
    TextView greetMsg, status;
    //StudentInfo studentInfo;
    LocalStorage localStorage;
    Gson gson;
    ArrayList<BroadcastMessageFormat> broadcastMessageFormatArrayList;
    Map<String, BroadcastMessageFormat> broadcastMessageFormatMap;
    ListView listView;
    StudentUser studentUser;
    FloatingActionButton scanButton;
    boolean listeningNow = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        greetMsg = findViewById(R.id.greet_msg);
        status = findViewById(R.id.status);
        localStorage = LocalStorage.getInstance(this);
        gson = new Gson();
        //studentInfo = gson.fromJson(localStorage.getString(Constants.STUDENT_INFO_KEY), StudentInfo.class);
        scanButton = findViewById(R.id.scan_button);
        broadcastMessageFormatArrayList = new ArrayList<>();
        broadcastMessageFormatMap = new HashMap<>();
        subjectListListener = setupMessageListener();
        listView = findViewById(R.id.teacher_list_view);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        studentUser = gson.fromJson(localStorage.getString(Constants.USER_INFO_KEY), StudentUser.class);
        //greetMsg.setText("Hello, " + studentUser.getName());

        final Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        scanButton.startAnimation(pulse);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listeningNow){
                    scanButton.startAnimation(pulse);
                    rippleBackground.stopRippleAnimation();
                    status.setText("Waiting for teacher's message...");
                    status.setVisibility(View.GONE);
                    greetMsg.setVisibility(View.VISIBLE);
                    Nearby.getMessagesClient(getApplicationContext()).unsubscribe(subjectListListener);
                    listeningNow = false;
                } else {
                    scanButton.clearAnimation();
                    rippleBackground.startRippleAnimation();
                    status.setText("Waiting for teacher's message...");
                    status.setVisibility(View.VISIBLE);
                    greetMsg.setVisibility(View.GONE);
                    startListening(subjectListListener);
                    listeningNow = true;
                }

            }
        });
    }


    private MessageListener setupMessageListener() {
        return new MessageListener() {
            @Override
            public void onFound(final Message message) {
                Log.d("Boo", "Found message: " + new String(message.getContent()));
                final String receivedString = new String(message.getContent());
                //Toast.makeText(StudentActivity.this, receivedString, Toast.LENGTH_SHORT).show();
                try {
                    /*Check if the received message is in correct format or not*/
                    BroadcastMessageFormat format = gson.fromJson(receivedString, BroadcastMessageFormat.class);

                    /*There might be multiple teachers starting the process of attendance
                     * so to show list of all teachers available following hashmap
                     * */
                    if(!broadcastMessageFormatMap.containsKey(format.getSubjectName())){
                        broadcastMessageFormatMap.put(format.getSubjectName(), format);
                        status.setText("Found " + broadcastMessageFormatMap.size() + " subjects, please select.");
                        showSubjectList();
                    }
                } catch (JsonSyntaxException e){
                    /*Message is not in correct format*/
                    Log.d("fuck all", e.getMessage());
                }

                /*We are going to unsubscribe and listen again,
                 * even if correct message is received there is possibility of two teachers
                 * starting process at same time.
                 * */
                Nearby.getMessagesClient(StudentActivity.this).unsubscribe(subjectListListener)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startListening(subjectListListener);
                            }
                        });
            }

            @Override
            public void onLost(Message message) {
                Toast.makeText(StudentActivity.this, "Message expired", Toast.LENGTH_SHORT).show();
                Log.d("Boo", "Lost sight of message: " + new String(message.getContent()));
            }
        };
    }

    private void showSubjectList() {
        final ArrayList<String> subjectNameArray = new ArrayList<>();

        for(BroadcastMessageFormat value : broadcastMessageFormatMap.values()){
            subjectNameArray.add(value.getSubjectName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                subjectNameArray
        );

        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Now that user has selected no need to listen for subject listeners so unsubscribe*/
                Nearby.getMessagesClient(StudentActivity.this).unsubscribe(subjectListListener);
                BroadcastMessageFormat broadcastMessage = broadcastMessageFormatMap.get(subjectNameArray.get(position));
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(broadcastMessage.getDatabaseToken());
                dbRef.child(studentUser.getRoll()).setValue(true);
                //listenToConfirmationMessage();
                //sendAttendanceToTeacher(subjectNameArray.get(position));
            }
        });

    }

/*
    private void listenToConfirmationMessage() {

    The problem with the confirmation messages is that
    By the time we receive attendance request from teacher other 50 students will also be sending their messages to the teacher
    so finding your specific confirmation message will be literally like finding a needle in the haystack
    so we will stick to one to many communication(Teacher to student)

        final MessageListener listener = new MessageListener(){
            @Override
            public void onFound(final Message message) {
                final String receivedString = new String(message.getContent());
                try{
                    FeedbackMessageFormat feedbackMessageFormat = gson.fromJson(receivedString, FeedbackMessageFormat.class);
                    Log.d("Fuck", receivedString);
                    if(feedbackMessageFormat.getSubject() != null && feedbackMessageFormat.getRoll() != null){
                        */
/*Message follows the feedback format, thus intended message*//*

                        if(feedbackMessageFormat.getRoll().equals(studentInfo.getRoll()) && feedbackMessageFormat.getSubject().equals(studentInfo.getSubject())){
                            */
/*Check if feeback message was intended for us & unsub the listener*//*

                            Nearby.getMessagesClient(StudentActivity.this).unsubscribe(subjectListListener);
                        } else {
                            */
/*if not for us throw exception and restart listening.*//*

                            throw new Exception();
                        }
                    } else {
                        */
/*not a feedback message*//*

                        throw new Exception();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Nearby.getMessagesClient(StudentActivity.this).unsubscribe(subjectListListener)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    listenToConfirmationMessage();
                                }
                            });
                }

            }

            @Override
            public void onLost(Message message) {
                Toast.makeText(StudentActivity.this, "Message expired", Toast.LENGTH_SHORT).show();

            }
        };
        startListening(listener);
    }
*/

/*    private void sendAttendanceToTeacher(String subject) {
        StudentInfo temp = new StudentInfo(
                studentInfo.getName(),
                studentInfo.getRoll(),
                studentInfo.getStudentClass(),
                studentInfo.getGrNo(),
                subject
        );

        String serializedJSON = gson.toJson(temp);
        sendMessage(serializedJSON);
    }


    private PublishOptions getPublishOptions(){
        return new PublishOptions
                .Builder()
                .setStrategy(Constants.broadcastStrategy)
                .setCallback(new PublishCallback(){
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Toast.makeText(StudentActivity.this, "Failed to send attendance.", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }

    private void sendMessage(String msg){
        Message mMessage = new Message((msg).getBytes());
        Nearby.getMessagesClient(StudentActivity.this).publish(mMessage, getPublishOptions())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StudentActivity.this, "Attendance Send", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentActivity.this, "Failed to send attendance.", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

    public SubscribeOptions getSubscribeOptions(){
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
    public void listenMessages(View v){
        startListening(subjectListListener);
    }

    private void startListening(MessageListener listener) {
        Nearby.getMessagesClient(StudentActivity.this).subscribe(listener, getSubscribeOptions())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StudentActivity.this, "Listening Messages.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentActivity.this, "Failed to listen to messages.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
