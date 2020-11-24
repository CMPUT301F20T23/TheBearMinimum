package com.example.bearminimum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


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
    private ArrayList<Notif> notifsList;
    //item click listener
    private ViewNotificationsAdapter.OnResultClickListener mOnResultClickListener;


    //constructor
    //needs a list of notifications to display
    public ViewNotificationsAdapter(ArrayList<Notif> notifsList, ViewNotificationsAdapter.OnResultClickListener onResultClickListener) {
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
        Notif currentNotif = notifsList.get(position);

        holder.titleView.setText(currentNotif.getTitle());
        holder.messageView.setText(currentNotif.getMessage());

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
