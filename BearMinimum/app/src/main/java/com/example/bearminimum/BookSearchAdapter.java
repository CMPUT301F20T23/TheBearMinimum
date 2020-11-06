package com.example.bearminimum;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BookSearchAdapter extends RecyclerView.Adapter<BookSearchAdapter.ViewHolder>{

    //holds books to display
    private ArrayList<Book> booksDisplayed;

    private FirebaseFirestore db;
    private String username;

    //item click listener
    private OnResultClickListener mOnResultClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView titleView;
        private TextView descrView;
        private TextView statusView;
        private TextView ownerView;

        //declare listener
        OnResultClickListener onResultClickListener;

        public ViewHolder(@NonNull View itemView, OnResultClickListener onResultClickListener) {
            super(itemView);

            //get textViews
            titleView = itemView.findViewById(R.id.book_title);
            descrView = itemView.findViewById(R.id.book_descr);
            statusView = itemView.findViewById(R.id.book_status);
            ownerView = itemView.findViewById(R.id.book_owner);
            this.onResultClickListener = onResultClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onResultClickListener.onResultClick(getAdapterPosition());
        }
    }

    public interface OnResultClickListener {
        void onResultClick(int position);
    }

    //constructor
    //needs a list of books to display
    public BookSearchAdapter(ArrayList<Book> bookList, OnResultClickListener onResultClickListener) {
        booksDisplayed = bookList;
        this.mOnResultClickListener = onResultClickListener;
    }

    @NonNull
    @Override
    public BookSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_book_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, mOnResultClickListener);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book currentBook = booksDisplayed.get(position);

        holder.titleView.setText(currentBook.getTitle());
        holder.descrView.setText(currentBook.getDescription());
        holder.statusView.setText(currentBook.getStatus());

        //get owner username
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentBook.getOwner())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                            holder.ownerView.setText(task.getResult().getString("username"));
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (booksDisplayed != null) {
            return booksDisplayed.size();
        } else {
            return 0;
        }
    }

   public void filteredList(ArrayList<Book> filteredList) {
        booksDisplayed = filteredList;
        notifyDataSetChanged();
   }
}
