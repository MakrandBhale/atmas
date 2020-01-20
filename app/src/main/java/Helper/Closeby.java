package Helper;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class Closeby {
    private Context context;
    private String TAG;
    private Message mMessage;
    private MessageListener messageListener = null;
    private SubscribeOptions subscribeOptions;

    public Closeby(Context context, Message mMessage, String TAG, SubscribeOptions subscribeOptions, MessageListener messageListener) {
        this.context = context;
        this.mMessage = mMessage;
        this.TAG = TAG;
        this.subscribeOptions = subscribeOptions;
        this.messageListener = messageListener;
    }

    public void sendMessage(){
        Nearby.getMessagesClient(context).publish(mMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Message sent");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "Message send error" + e.toString());
                    }
                });
    }

    public void subscribe(){
        Nearby.getMessagesClient(context).subscribe(messageListener, subscribeOptions)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, e.toString());
                        //Toast.makeText(StudentActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Subscribed");
                        //Toast.makeText(StudentActivity.this, "Subscribed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void unsubscribe(){
        Nearby.getMessagesClient(context).unsubscribe(messageListener);
        Nearby.getMessagesClient(context).unpublish(mMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Unsubscribed");
                    }
                });
    }



    public void ringtone(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}