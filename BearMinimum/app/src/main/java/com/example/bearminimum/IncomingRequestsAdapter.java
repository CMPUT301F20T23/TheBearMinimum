package com.example.bearminimum;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * IncomingRequestsAdapter
 *
 * This class is a custom list adapter for showing current requests on a book
 *
 * Nov. 6, 2020
 */
public class IncomingRequestsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<User> list;
    private Context context;
    private String bookid;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public IncomingRequestsAdapter(ArrayList<User> list, Context context, String bookid) {
        this.list = list;
        this.context = context;
        this.bookid = bookid;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.incoming_reqs_layout, null);
        }

        TextView requesterName = view.findViewById(R.id.requester_name);
        requesterName.setText(list.get(position).getUsername());

        Button declineButton = view.findViewById(R.id.decline_req_button);
        Button acceptButton = view.findViewById(R.id.accept_req_button);

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineReq(list.get(position).getUid(), position);
                notifyDataSetChanged();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptReq(list.get(position).getUid(), position);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    /**
     * accept a request on a book, and decline all others
     * @param userid    id of the accepted requester
     * @param position  int position of the request accepted
     */
    private void acceptReq(String userid, int position) {

        //get all current requests on the book,
        //send reject notification and unsubscribe from topic
        db.collection("books").document(bookid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){

                            //get requester id list, and remove accepted user
                            List<String> requestIds = (List<String>) task.getResult().get("requests");
                            requestIds.remove(userid);

                            //get topic string (ownerId/bookId)
                            String ownerId = task.getResult().get("owner").toString();
                            String topic = ownerId+"-"+bookid;

                            //create reject NotificationObject to pass in
                            String rtitle = "request declined";
                            String rbody = ownerId+"-"+bookid+"-"+userid;
                            NotificationObject rejNotif = new NotificationObject(topic, rtitle, rbody, 3);

                            //send reject notification
                            SendNotification.sendToTopic(rejNotif);

                            //create accept NotificationObject to pass in
                            String atitle = "request accepted";
                            String abody = ownerId+"-"+bookid+"-"+userid;
                            NotificationObject accNotif = new NotificationObject(topic, atitle, abody, 2);

                            //send accept notification
                            SendNotification.sendToUser(accNotif, userid);

                            //for each requester, remove from topic
                            //check if there's more than 1 id
                            //(in case the only id is the accepted requester's id)
                            if (requestIds.size() >= 1) {
                                for (String id : requestIds) {
                                    TopicSubscription.unsubscribeToTopic(topic, id);
                                }
                                Log.d(TAG, "book request accepted, successfully unsubbed other requesters");
                            }
                        } else {
                            Log.d(TAG, "couldn't get book document");
                        }
                    }
                });

        //accept the request, decline all others, set status
        ArrayList<String> temp = new ArrayList<>();
        temp.add(userid);
        db.collection("books").document(bookid).update("requests", temp, "status", "accepted");
        User tempUser = list.get(position);
        list.clear();
        list.add(tempUser);
    }

    /**
     * decline a request on a book
     * @param userid    id of user who's request was declined
     * @param position  int position of the request declined
     */
    private void declineReq(String userid, int position) {
        //decline the request, if no more requesters set status
        db.collection("books").document(bookid).update("requests", FieldValue.arrayRemove(userid));
        list.remove(position);

        db.collection("books").document(bookid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        //get topic string (ownerId/bookId)
                        String ownerId = task.getResult().get("owner").toString();
                        String topic = ownerId+"-"+bookid;

                        //create reject NotificationObject to pass in
                        String title = "request declined";
                        String body = ownerId+"-"+bookid+"-"+userid;
                        NotificationObject newNotif = new NotificationObject(topic, title, body, 3);

                        //send reject notification
                        SendNotification.sendToUser(newNotif, userid);
                    }
                });

    }
}
