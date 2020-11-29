package com.example.bearminimum;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.bearminimum.App.CHANNEL_ID;

/**
 * SendNotification
 *
 * Sends notifications to users or subscribers of a topic
 * Can also display the notification as a push notification
 *
 * Nov. 29, 2020
 */


public class SendNotification {

    /**
     * shows the notification
     */

    public static void displayNotification (Context context, String title, String body) {

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, notification);

    }


    /**
     * Sends a notification to a single user
     * @param notif         a notification object (NotificationObject class)
     * @param receiverId    String uid of the receiver of this notification
     */

    public static void sendToUser (NotificationObject notif, String receiverId) {
        //Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //get info from the notification object
        String title = notif.getTitle();
        String ownerId = notif.getOwnerId();
        String bookId = notif.getBookId();
        String topic = notif.getTopic();
        String requesterId = notif.getRequesterId();
        String type = String.valueOf(notif.getType());
        
        //create notification
        String body = ownerId+"-"+bookId+"-"+requesterId;
        String notification = topic+"."+title+"."+body+"."+type;

        //add notification to specified receiver
        DocumentReference receiverDoc = db.collection("notifications").document(receiverId);

        //check if doc exists
        receiverDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        //create and store firestore document
                        Map<String, Object> newNotifDoc = new HashMap<>();
                        newNotifDoc.put("notifications", Arrays.asList(notification));
                        receiverDoc.set(newNotifDoc).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "successfully sent notification to receiver " + receiverId);
                                } else {
                                    Log.d(TAG, "failed to send notification to receiver " + receiverId);
                                }
                            }
                        });
                    } else {
                        //doc exists so add value in
                        receiverDoc.update("notifications", FieldValue.arrayUnion(notification))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "successfully sent notification to receiver " + receiverId);
                                    }
                                });
                    }
                }
            }
        });
    }


    /**
     * Sends notifications to all users subscribed to a topic
     * @param notif         a notification object (NotificationObject class)
     */
    public static void sendToTopic (NotificationObject notif) {
        //Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //get info from the notification object
        //  we can use one notif to generalize for all because
        //  the only time we send messages to a topic is if the
        //  notification is a reject notification
        String title = notif.getTitle();
        String ownerId = notif.getOwnerId();
        String bookId = notif.getBookId();
        String type = String.valueOf(notif.getType());
        String topic = notif.getTopic();

        //store subscriber ids
        ArrayList<String> subscriberIds = new ArrayList<>();

        //get all subscribers of the given topic
        db.collection("topics")
                .document(topic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> ids = (List<String>) task.getResult().get("subscribers");

                            //save all ids to our list
                            for (String id : ids) {
                                subscriberIds.add(id);
                            }

                            Log.d(TAG, "successfully obtained subscribers from topic " + topic);
                        } else {
                            Log.d(TAG, "failed to get subscribers from topic" + topic);
                        }
                    }
                });

        //for all users obtained, send a notification
        for (String receiver : subscriberIds) {
            //create notification
            String body = ownerId+"-"+bookId+"-"+receiver;
            String notification = topic+"."+title+"."+body+"."+type;
            
            DocumentReference receiverDoc = db.collection("notifications").document(receiver);
            receiverDoc.update("notifications", FieldValue.arrayUnion(notification))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "successfully sent notification to receiver " + receiver);
                            } else {
                                Log.d(TAG, "failed to send notification to receiver " + receiver);
                            }
                        }
                    });
        }

    }

}
