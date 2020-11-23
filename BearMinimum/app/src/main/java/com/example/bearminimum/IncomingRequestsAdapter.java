package com.example.bearminimum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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

    private void acceptReq(String userid, int position) {
        //accept the request, decline all others, set status
        ArrayList<String> temp = new ArrayList<>();
        temp.add(userid);
        FirebaseFirestore.getInstance().collection("books").document(bookid).update("requests", temp, "status", "accepted");
        User tempUser = list.get(position);
        list.clear();
        list.add(tempUser);
    }

    private void declineReq(String userid, int position) {
        //decline the request, if no more requesters set status
        FirebaseFirestore.getInstance().collection("books").document(bookid).update("requests", FieldValue.arrayRemove(userid));
        list.remove(position);
    }
}
