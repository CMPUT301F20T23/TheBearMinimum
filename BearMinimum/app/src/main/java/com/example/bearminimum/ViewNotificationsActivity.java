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

import java.util.ArrayList;
import java.util.List;

public class ViewNotificationsActivity extends AppCompatActivity implements ViewNotificationsAdapter.OnResultClickListener{

    private static final String TAG = "view_notifs";
    //to display notifications
    private ArrayList<NotificationObject> notifList;
    private ViewNotificationsAdapter notifAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notifications);

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
        notifList = new ArrayList<>();

        //get current user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get all notifications under user
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //get all notifications
                            List<String> notifications = (List<String>) task.getResult().get("notifications");

                            //for each item, parse and create a notification object
                            for (String aNotif : notifications) {
                                //split the notification to get the info
                                String[] separated = aNotif.split("/");
                                String topic = separated[0];
                                String title = separated[1];
                                String body = separated[2];
                                int type = Integer.valueOf(separated[3]);

                                NotificationObject newNotif = new NotificationObject(topic,title,body,type);
                                notifList.add(newNotif);
                            }

                            Log.d(TAG, "successfully got notifications for user " + userId);

                        } else {
                            Log.d(TAG, "failed to get notifications for user " + userId);
                        }
                    }
                });

    }

    @Override
    public void onResultClick(int position) {
        NotificationObject sendNotification = notifList.get(position);
    }
}
