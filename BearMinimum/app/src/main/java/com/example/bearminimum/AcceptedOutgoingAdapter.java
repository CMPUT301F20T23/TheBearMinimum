package com.example.bearminimum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


/**
 * AcceptedOutgoingAdapter
 *
 * basic adapter extending RecyclerView.Adapter
 * custom ViewHolder to access our views
 * Used to display books in the MainActivity RecyclerView
 *
 */

public class AcceptedOutgoingAdapter extends RecyclerView.Adapter<AcceptedOutgoingAdapter.ViewHolder> {
    /**
     * Interface
     * OnBookClickListener
     *
     */
    public interface OnBookClickListener {
        void onBookClick(int position, String owner);
    }


    //item click listener
    private OnBookClickListener mOnBookClickListener;

    //member variable for book objects
    private List<Book> mBooks;


    //pass in the book array to the constructor
    public AcceptedOutgoingAdapter(List<Book> books, OnBookClickListener onBookClickListener) {
        mBooks = books;
        this.mOnBookClickListener = onBookClickListener;
    }

    //direct reference to each view within a data item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //declare listener
        OnBookClickListener onBookClickListener;

        //member variable for views rendered as rows
        public TextView bookTextView;
        public TextView userNameTextView;
        public TextView statusTextView;

        //constructor that accepts new row
        //view lookups to find each subview
        public ViewHolder(View itemView, OnBookClickListener onBookClickListener) {
            super(itemView);

            bookTextView = (TextView) itemView.findViewById(R.id.aor_book_name);
            userNameTextView = (TextView) itemView.findViewById(R.id.aor_owner);
            statusTextView = (TextView) itemView.findViewById(R.id.aor_location_status);
            this.onBookClickListener = onBookClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBookClickListener.onBookClick(getAdapterPosition(), mBooks.get(getAdapterPosition()).getOwner());
        }
    }



    //adapter requires three primary methods
    //onCreateViewHolder to inflate and create the holder
    //onBindViewHolder to set the view attributes based on data
    //getItemCount to determine number of items

    @Override
    public AcceptedOutgoingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the custom layout
        View contactView = inflater.inflate(R.layout.accepted_outgoing_list, parent, false);

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView, mOnBookClickListener);
        return viewHolder;
    }

    //populate data into the item through holder
    @Override
    public void onBindViewHolder(AcceptedOutgoingAdapter.ViewHolder holder, int position) {

        //get data based on position
        Book book = mBooks.get(position);

        //set item views based on views and data
        TextView textView1 = holder.bookTextView;
        textView1.setText(book.getTitle());
        TextView textView2 = holder.userNameTextView;
        TextView textView3 = holder.statusTextView;
        FirebaseFirestore.getInstance().collection("books").document(book.getBid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    String locationStatus = (String) value.getData().get("latitude");
                    if (locationStatus.length() > 0) {
                        textView3.setText("location status: selected");
                    } else {
                        textView3.setText("location status: unselected");
                    }
                }
            }
        });

        //show owner name from firebase
        FirebaseFirestore.getInstance().collection("users").document(book.getOwner()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            textView2.setText("owned by: " + (String) task.getResult().getData().get("username"));
                        } else
                            textView2.setText("owned by: failed to fetch");
                    }
                });
    }



    //return total count of items in the list
    @Override
    public int getItemCount() {

        return mBooks.size();
    }

}