package com.example.bearminimum;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static android.content.ContentValues.TAG;
import static me.pushy.sdk.config.PushyNotificationChannel.CHANNEL_ID;


public class App extends Application {
    private static final String CHANNEL_ID = "request_channel";
    private NotificationManagerCompat notificationManager;
    private FirebaseFirestore db;
    private Context context;
    private String requester;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        createNotificationChannel();

        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();

        notificationManager = NotificationManagerCompat.from(this);
        getNotifications();


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Request";
            String description = "A request has been placed on your book";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getNotifications() {

        //get user id
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentUser.getUid();

        //get notifications from db
        db.collection("notifications")
                .whereEqualTo("uid", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "got books");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int post_id = 1;
                                List<String> requestList = (List<String>) document.getData().get("requests");
                                for (String requesterID : requestList) {

                                    db.collection("users")
                                            .document(requesterID)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful())
                                                        requester = task.getResult().getString("username");
                                                }
                                            });


                                    String title = "book request";
                                    String message = requester + " has requested one of your books!";
                                    Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_stat_ic_notification)
                                            .setContentTitle(title)
                                            .setContentText(message)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                            .build();
                                    notificationManager.notify(post_id, notification);
                                    post_id += 1;
                                }

                            }
                        } else {
                            Log.d(TAG, "error getting notification documents");

                        }
                    }
                });



    }



}
