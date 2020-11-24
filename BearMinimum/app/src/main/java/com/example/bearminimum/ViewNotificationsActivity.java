package com.example.bearminimum;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewNotificationsActivity extends AppCompatActivity implements ViewNotificationsAdapter.OnResultClickListener{

    private static final String TAG = "view_notifs";
    //to display notifications
    private ArrayList<Notif> notifList;
    private ViewNotificationsAdapter notifAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private NotificationManagerCompat notificationManagerCompat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notifications);

        notificationManagerCompat = NotificationManagerCompat.from(this);



        //TODO:
        // pass in intent bundle with list of notifs from main

        getNotifs();
        setUpViewNotificationAdapter();
        recyclerView.setAdapter(notifAdapter);
        
        for (Notif notif : notifList) {
            notif.sendNotif(getApplicationContext(), notificationManagerCompat);
        }
    }


    /**
     * Sets up a ViewNotification adapter for the RecyclerView
     */

    private void setUpViewNotificationAdapter() {
        recyclerView = findViewById(R.id.notification_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        notifAdapter = new ViewNotificationsAdapter(notifList, this);

        recyclerView.setLayoutManager(layoutManager);
    }


    /**
     * gets notifications under current user
     */

    private void getNotifs() {
        notifList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        //current user
        String userId = FirebaseAuth.getInstance().getUid();

        //get all requests for user's book
        db.collection("notifications")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "got document");
                            ArrayList<String> requests = (ArrayList<String>) task.getResult().get("requests");

                            //process into notif instances
                            int notif_id = 1;
                            for (String requestID : requests) {
                                notifList.add(new Notif(notif_id, requestID));
                                notif_id += 1;
                                Log.d(TAG, "processing: " + requestID);
                            }
                            Log.d(TAG, "done all request notifications " + notifList.size() );
                        } else {
                            Log.d(TAG, "couldn't get document");
                        }

                        //notify adapter list changed
                        notifAdapter.notifyDataSetChanged();
                    }
                });


        Log.d(TAG, "does list still exist " + notifList);



        //TODO:
        // get notifications for book requested by user (accepted or declined)


    }

    @Override
    public void onResultClick(int position) {
        Notif notif = notifList.get(position);
    }
}
