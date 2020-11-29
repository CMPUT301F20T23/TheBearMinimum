package com.example.bearminimum;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


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
     * returns the body of the notification
     * @return      String of the notification body
     */

    public String getBody() {

        //build the correct body for the notification type
        if (type == 1) {
            //request notification
            body = requestBody();
        } else if (type == 2) {
            //accept notification
            body = acceptBody();
        } else if (type == 3) {
            //reject notification
            body = rejectBody();
        }

        return body;
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

    /**
     * Creates the body for a request notification
     * @return      String of the completed notification body
     */

    private String requestBody() {

        //get requesterId username and bookId title
        String bUsername = getUsername(requesterId);
        String bookTitle = getBookTitle(bookId);

        //build message
        String message = bUsername + "has requested your book" + bookTitle;
        return message;
    }

    /**
     * Creates the body for a accept notification
     * @return      String of the completed notification body
     */

    private String acceptBody() {

        //get requesterId username and bookId title
        String oUsername = getUsername(ownerId);
        String bookTitle = getBookTitle(bookId);

        //build message
        String message = oUsername + "has accepted your request for " + bookTitle;
        return message;
    }

    /**
     * Creates the body for a reject notification
     * @return      String of the completed notification body
     */

    private String rejectBody() {

        //get requesterId username and bookId title
        String oUsername = getUsername(ownerId);
        String bookTitle = getBookTitle(bookId);

        //build message
        String message = oUsername + "has rejected your request for " + bookTitle;
        return message;
    }

    /**
     * Finds and returns the username of a user from the given uid
     * @param uid
     * @return      String of the user's username
     */

    private String getUsername(String uid) {
        final String[] username = new String[1];
        db.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            username[0] = task.getResult().get("username").toString();

                        }
                    }
                });
        return username[0];
    }

    /**
     * Finds and returns the book title of the given book id
     * @param bid
     * @return      String of the book's title
     */
    private String getBookTitle(String bid) {
        final String[] bookTitle = new String[1];
        db.collection("books").document(bid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            bookTitle[0] = task.getResult().get("title").toString();
                        }
                    }
                });
        return bookTitle[0];
    }



}
