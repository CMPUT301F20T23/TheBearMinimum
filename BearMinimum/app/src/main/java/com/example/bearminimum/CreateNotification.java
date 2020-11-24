package com.example.bearminimum;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static android.content.ContentValues.TAG;
import static me.pushy.sdk.config.PushyNotificationChannel.CHANNEL_ID;

public class CreateNotification {
    private NotificationManagerCompat notificationManager;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> requestIds;
    private String requestId;
    private String userID;
    private List<String> requestList;


    public CreateNotification(NotificationManagerCompat notificationManager, Context context, String userID) {
        this.notificationManager = notificationManager;
        this.context = context;
        this.userID = userID;
    }

    public void getNotifications() {

        //get notifications from db
        db.collection("notifications")
                .whereEqualTo("uid", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "got notification doc");
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                requestList = (List<String>) document.getData().get("requests");

                            }
                        } else {
                            Log.d(TAG, "error getting notification documents");

                        }
                    }
                });


        if (requestList != null) {
            for (String user : requestList) {

                db.collection("users")
                        .document(user)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful())
                                    requestId = task.getResult().getString("username");
                                requestIds.add(requestId);
                            }
                        });
            }

            //TODO: fix notification ID
            for (String requester : requestIds) {

                String title = "book request";
                String message = requester + " has requested one of your books!";
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                notificationManager.notify(1, notification);


            }
        }



    }
}
