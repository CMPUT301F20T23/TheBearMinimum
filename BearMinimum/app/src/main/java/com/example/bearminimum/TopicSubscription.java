package com.example.bearminimum;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TopicSubscription
 *
 * Subscribes or unsubscribe users from a given topic
 *
 * Nov. 28, 2020
 */

public class TopicSubscription {

    //variables
    private static final String TAG = "TopicSubscription";


    /**
     * Subscribes the user to the given topic
     * @param uid           the subscriber's uid
     * @param topic         the topic to subscribe to
     */
    public static void subscribeToTopic (String topic, String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //add user to the topic doc
        DocumentReference topicDoc = db.collection("topics").document(topic);

        //check if doc exists
        topicDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().exists()) {
                        //create and store firestore document
                        Map<String, Object> newTopic = new HashMap<>();
                        newTopic.put("subscribers", Arrays.asList(uid));
                        topicDoc.set(newTopic).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, uid + " added to newly created topic");
                                } else {
                                    Log.d(TAG, topic + " topic couldn't be created. Subscription failed");
                                }
                            }
                        });
                    } else {
                        //doc exists so add value in
                        topicDoc.update("subscribers", FieldValue.arrayUnion(uid))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, uid + " successfully subscribed to topic");
                                    }
                                });
                    }
                }
            }
        });


        //add topic to user doc
        DocumentReference userDoc = db.collection("users").document(uid);
        userDoc.update("subscription", FieldValue.arrayUnion(topic))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, topic + " successfully added to user subscriptions");
                    }
                });
    }

    /**
     * Unsubscribe the user from the give topic
     * @param uid           the subscriber's uid
     * @param topic         the topic to subscribe to
     */
    public static void unsubscribeToTopic (String topic, String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //remove user from the topic doc
        DocumentReference topicDoc = db.collection("topics").document(topic);
        topicDoc.update("subscribers", FieldValue.arrayRemove(uid))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, uid + " successfully unsubscribed from topic");
                    }
                });

        //if topic has no more subscribers, delete the topic doc
        topicDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> subscribers = (List<String>) task.getResult().get("subscribers");
                    if (subscribers.isEmpty()) {
                        if (topicDoc.delete().isSuccessful()) {
                            Log.d(TAG, "topic has no more subscribers, successfully deleted topic");
                        }
                    }
                }
            }
        });

        //remove topic from user doc
        DocumentReference userDoc = db.collection("users").document(uid);
        userDoc.update("subscription", FieldValue.arrayRemove(topic))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, topic + " successfully removed from user subscriptions");
                    }
                });
    }

}
