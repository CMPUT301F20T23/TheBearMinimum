package com.example.bearminimum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookSearchAdapter extends RecyclerView.Adapter<BookSearchAdapter.ViewHolder>{

    //holds books to display
    private ArrayList<Book> booksDisplayed;


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleView;
        private TextView descrView;
        private TextView statusView;
        private TextView ownerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //get textViews
            titleView = itemView.findViewById(R.id.book_title);
            descrView = itemView.findViewById(R.id.book_descr);
            statusView = itemView.findViewById(R.id.book_status);
            ownerView = itemView.findViewById(R.id.book_owner);
        }
    }

    //constructor
    //needs a list of books to display
    public BookSearchAdapter(ArrayList<Book> bookList) {
        booksDisplayed = bookList;
    }

    @NonNull
    @Override
    public BookSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_book_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book currentBook = booksDisplayed.get(position);

        holder.titleView.setText(currentBook.getTitle());
        holder.descrView.setText(currentBook.getDescription());
        holder.statusView.setText(currentBook.getStatus());
        holder.ownerView.setText(currentBook.getOwner());
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
