package com.example.bearminimum;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Notif {
    private static final String CHANNEL_ID = "request_channel";
    private static final String TAG = "NOTIF CREATION";
    private int notif_id;
    private String requesterUid;
    private String requesterUsername;
    private String title;
    private String message;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public Notif(int notif_id, String requesterUid) {
        this.notif_id = notif_id;
        this.requesterUid = requesterUid;
        this.title = "book request";

        //get username of requester
        db.collection("users")
                .document(requesterUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            //TODO
                            // requesterUsername becomes null outside
                            requesterUsername = task.getResult().getString("username");
                            Log.d(TAG, "username: " + requesterUsername);
                        } else {
                            Log.d(TAG, "failed to get doc for username");
                        }
                    }
                });

        this.message = requesterUsername + " has requested one of your books!";
    }

    /**
     * returns the id of the notification
     * @return      int of notification id
     */

    public int getNotif_id() {
        return notif_id;
    }

    /**
     * returns the uid of the requester
     * @return      String of the requester's uid
     */

    public String getRequesterUid() {
        return requesterUid;
    }

    /**
     * returns the title of the notification
     * @return      String of the notification title
     */

    public String getTitle() {
        return title;
    }

    /**
     * returns the message of the notification
     * @return      String of the notification message
     */

    public String getMessage() {
        return message;
    }



    /**
     * sends the notification
     */

    public void sendNotif (Context context, NotificationManagerCompat notificationManager) {

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(notif_id, notification);

    }
}
