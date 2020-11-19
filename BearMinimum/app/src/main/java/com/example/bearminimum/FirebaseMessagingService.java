package com.example.bearminimum;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

//https://medium.com/androidcodelab/firebase-cloud-messaging-or-push-notification-fb142fbcf270
//https://firebase.google.com/docs/reference/android/com/google/firebase/messaging/RemoteMessage

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public static final String TAG = "MessagingService";
    FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

    @Override
    public void onNewToken(String token){
        super.onNewToken(token);
        Log.d("NEW_TOKEN", token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);  //need to implement this
    }

    /**
     * Returns the current token
     * @return     a String of the token
     */

    public String getToken() {
        final String[] token = new String[1];
        firebaseMessaging.getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token[0] = task.getResult();

                    }
                });
        return token[0];
    };

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public void sendRequest (String recipient) {
        String token = getToken();

        RemoteMessage.Builder message = new RemoteMessage.Builder(recipient + "@gcm.googleapis.com")
                .setMessageId(token + System.currentTimeMillis())
                .addData("Title", "Request")
                .addData("Message", "Book requested");

        firebaseMessaging.send(message.build());
    }


}
