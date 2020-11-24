package com.example.bearminimum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        FirebaseFirestore.getInstance().collection("users")
                .document(currentNotif.getRequesterUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            holder.messageView.setText(task.getResult().getString("username") + currentNotif.getMessage());
                        } else {
                            holder.messageView.setText("could not fetch requester username");
                        }
                    }
                });


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
