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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NotificationManagerCompat notificationManagerCompat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notifications);

        notifList = new ArrayList<Notif>();
        Notif aNotif = new Notif(4, "RSmeBy2N5kPmCoCkT2IIdwUyIvz1");
        notifList.add(aNotif);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        aNotif.sendNotif(getApplicationContext(), notificationManagerCompat);


        //TODO:
        // pass in intent bundle with list of notifs from main

        getNotifs();
        setUpViewNotificationAdapter();
        recyclerView.setAdapter(notifAdapter);
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
        //current user
        String userId = FirebaseAuth.getInstance().getUid();

        //get all requests for user's book
        db.collection("notifications")
                .whereEqualTo("uid", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "got document");
                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                ArrayList<String> requests = (ArrayList<String>) doc.get("requests");

                                //TODO:
                                // WHY ARE THINGS NOT SAVING
                                //process into notif instances
                                int notif_id = 1;
                                for (String requestID : requests) {
                                    Notif new_notif = new Notif(notif_id, requestID);
                                    notifList.add(new_notif);
                                    notif_id += 1;
                                    Log.d(TAG, "processing: " + new_notif.getRequesterUid());
                                }
                                Log.d(TAG, "done all request notifications " + notifList );
                            }
                        } else {
                            Log.d(TAG, "couldn't get document");
                        }
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
