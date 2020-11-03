package com.example.bearminimum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// this class is use tod define an List Adapter to display gear information on screen nicely
public class BookAdapter extends ArrayAdapter<Book> {
    private ArrayList<Book> books;
    private Context context;

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context,0,books);
        this.books = books;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;


        if (view == null){
            // need the layout update and link to the layoutName
            //view = LayoutInflater.from(context).inflate(R.layout.LAYOUTNAME,parent,false);
        }


        Book book = books.get(position);


        // ***************need the layout first*********************
        //TextView bookName = view.findViewById(R.id.bookName);

        //TextView bookState = view.findViewById(R.id.bookState);

        //TextView bookBorrower = view.findViewById(R.id.bookBorrower);

        //bookName.setText(book.getTitle());

        //bookBorrower.setText(book.getBorrower());
        if(book.getStatus() == "Borrowed") {
            //bookState.setText("Borrowed");
        }
        else if (book.getStatus() == "UNBorrowed"){
            //bookState.setText("UNBorrowed");
        }

        return view;
    }



}
