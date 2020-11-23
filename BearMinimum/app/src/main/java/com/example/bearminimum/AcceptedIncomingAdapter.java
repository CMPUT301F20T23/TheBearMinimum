package com.example.bearminimum;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



/**
 * AcceptedIncomingAdapter
 *
 * basic adapter extending RecyclerView.Adapter
 * custom ViewHolder to access our views
 * Used to display books in the MainActivity RecyclerView
 *
 */

public class AcceptedIncomingAdapter extends RecyclerView.Adapter<AcceptedIncomingAdapter.ViewHolder> {
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
    public AcceptedIncomingAdapter(List<Book> books, OnBookClickListener onBookClickListener) {
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
        public Button scanButton;

        //constructor that accepts new row
        //view lookups to find each subview
        public ViewHolder(View itemView, OnBookClickListener onBookClickListener) {
            super(itemView);

            bookTextView = (TextView) itemView.findViewById(R.id.ar_book_name);
            userNameTextView = (TextView) itemView.findViewById(R.id.ar_requester);
            statusTextView = (TextView) itemView.findViewById(R.id.ar_location_status);
            scanButton = (Button) itemView.findViewById(R.id.ar_scan_button);
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
    public AcceptedIncomingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the custom layout
        View contactView = inflater.inflate(R.layout.accepted_incoming_list, parent, false);

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView, mOnBookClickListener);
        return viewHolder;
    }

    //populate data into the item through holder
    @Override
    public void onBindViewHolder(AcceptedIncomingAdapter.ViewHolder holder, int position) {

        //get data based on position
        Book book = mBooks.get(position);
        Log.d("MyDebug", book.getLatitude());

        //set item views based on views and data
        TextView textView1 = holder.bookTextView;
        textView1.setText(book.getTitle());
        TextView textView2 = holder.userNameTextView;
        TextView textView3 = holder.statusTextView;
        String lStatus = "";
        if (book.getLatitude().equals(""))
            lStatus = "unselected";
        else
            lStatus = "longitude " + book.getLongitude() + ", latitude " + book.getLatitude();
        textView3.setText("location status: " + lStatus);

        FirebaseFirestore.getInstance().collection("books").document(book.getBid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String requester = ((List<String>) task.getResult().getData().get("requests")).get(0);
                            FirebaseFirestore.getInstance().collection("users").document(requester).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    textView2.setText("requested by: " + task.getResult().getData().get("username"));
                                }
                            });
                        } else
                            textView2.setText("requested by: failed to fetch");
                    }
                });

        Button scanBtn = holder.scanButton;
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch scan activity
                Log.d("MyDebug", "book scan requested");
            }
        });
    }

    //return total count of items in the list
    @Override
    public int getItemCount() {

        return mBooks.size();
    }

}