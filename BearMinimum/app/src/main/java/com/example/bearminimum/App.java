package com.example.bearminimum;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.google.firebase.FirebaseApp;


/**
 * App
 *
 * Creates the notification channel for the app during
 * app start up
 *
 * Nov. 23, 2020
 */

public class App extends Application {
    public static final String CHANNEL_ID = "book_requests_channel";


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        createNotificationChannel();

    }

    /**
     * Creates a notification channel so notifications can
     * be sent later within the app
     */

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





}
