package com.example.bearminimum;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationObject {
    //variables
    private String topic;
    private String title;
    private String body;
    private int type;   //indicates what notification
                        // 1 = request
                        // 2 = accept
                        // 3 = decline

    //obtained from splitting topic
    private String ownerId;
    private String bookId;
    private String requesterId;


    public NotificationObject(String topic, String title, String body, int type) {
        this.topic = topic;
        this.title = title;
        this.body = body;
        this.type = type;

        //split body to get the info
        String[] separated = body.split("-");
        this.ownerId = separated[0];
        this.bookId = separated[1];
        this.requesterId = separated[2];
    }

    /**
     * returns the topic of this notification
     * @return      String of the notification topic
     */

    public String getTopic() {
        return topic;
    }

    /**
     * returns the title of the notification
     * @return      String of the notification title
     */

    public String getTitle() {
        return title;
    }

    /**
     * returns the uid of the book's owner of the topic
     * @return      String of the owner's id
     */

    public String getOwnerId() {
        return ownerId;
    }

    /**
     * returns the uid of the requester
     * @return      String of the requester's id
     */

    public String getRequesterId() {return requesterId;}

    /**
     * returns the bid of the book of the topic
     * @return      String of the book id
     */

    public String getBookId() {
        return bookId;
    }

    /**
     * returns the type of the notification
     * @return      int of the notification type
     */

    public int getType() {return type;}


}
