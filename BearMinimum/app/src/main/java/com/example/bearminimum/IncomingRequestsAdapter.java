package com.example.bearminimum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IncomingRequestsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<User> list;
    private Context context;

    public IncomingRequestsAdapter(ArrayList<User> list, Context context) {
        this.list = list;
        this.context = context;
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
                //decline the request, if no more requesters set status
                notifyDataSetChanged();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //accept the request, decline all others, set status
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
