package com.example.bearminimum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ViewNotificationsAdapter extends RecyclerView.Adapter<ViewNotificationsAdapter.ViewHolder> {

    /**
     * Interface
     * OnResultClickListener
     */
    public interface OnResultClickListener {
        void onResultClick(int position);
    }

    //holds books to display
    private ArrayList<NotificationObject> notifsList;
    //item click listener
    private ViewNotificationsAdapter.OnResultClickListener mOnResultClickListener;
    //Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    //constructor
    //needs a list of notifications to display
    public ViewNotificationsAdapter(ArrayList<NotificationObject> notifsList, ViewNotificationsAdapter.OnResultClickListener onResultClickListener) {
        this.notifsList = notifsList;
        this.mOnResultClickListener = onResultClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView titleView;
        private TextView messageView;

        //declare listener
        ViewNotificationsAdapter.OnResultClickListener onResultClickListener;

        public ViewHolder(@NonNull View itemView, ViewNotificationsAdapter.OnResultClickListener onResultClickListener) {
            super(itemView);

            //get textViews
            titleView = itemView.findViewById(R.id.notif_title);
            messageView = itemView.findViewById(R.id.notif_message);
            this.onResultClickListener = onResultClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onResultClickListener.onResultClick(getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public ViewNotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        ViewNotificationsAdapter.ViewHolder viewHolder = new ViewNotificationsAdapter.ViewHolder(v, mOnResultClickListener);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewNotificationsAdapter.ViewHolder holder, int position) {
        NotificationObject currentNotif = notifsList.get(position);

        holder.titleView.setText(currentNotif.getTitle());

        //get info from notification object
        int type = Integer.valueOf(currentNotif.getType());
        String requesterId = currentNotif.getRequesterId();
        String ownerId = currentNotif.getOwnerId();
        String bookId = currentNotif.getBookId();

        Task<DocumentSnapshot> bookTask = db.collection("books").document(bookId).get();

        if (type == 1) {
            //is a request notification

            db.collection("users").document(requesterId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                //get username
                                String username = task.getResult().get("username").toString();

                                //get book title
                                if (bookTask.isSuccessful()) {
                                    String bookTitle = bookTask.getResult().get("title").toString();

                                    //build message
                                    String body = username + " has requested your book " + bookTitle;
                                    holder.messageView.setText(body);

                                }
                            }
                        }
                    });

        } else if (type == 2) {
            //is an accept notification

            db.collection("users").document(ownerId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                //get username
                                String username = task.getResult().get("username").toString();

                                //get book title
                                if (bookTask.isSuccessful()) {
                                    String bookTitle = bookTask.getResult().get("title").toString();

                                    //build message
                                    String body = username + " has accepted your request for " + bookTitle;
                                    holder.messageView.setText(body);

                                }
                            }
                        }
                    });

        } else if (type == 3) {
            //is a reject notification

            db.collection("users").document(ownerId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                //get username
                                String username = task.getResult().get("username").toString();

                                //get book title
                                if (bookTask.isSuccessful()) {
                                    String bookTitle = bookTask.getResult().get("title").toString();

                                    //build message
                                    String body = username + " has rejected your request for " + bookTitle;
                                    holder.messageView.setText(body);

                                }
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        if (notifsList != null) {
            return notifsList.size();
        } else {
            return 0;
        }
    }

}
