package com.example.bearminimum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

//assumes Book class exists
public class NavigationListAdapter extends ArrayAdapter<Book>{

    //attributes
    private Context mContext;
    private int mResource;

    //constructor
    //context is MainActivity
    //resource is navigation_list.xml
    //object is list of books
    public NavigationListAdapter(Context context, int resource, ArrayList<Book> objects){
        //explicit call to super
        super(context, resource, objects);

        //initialize context and resouce
        mContext = context;
        mResource = resource;
    }

    //get the view and convert to listview
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //get the book information
        //assume book name, username, status for V1
        //getters may have different names
        String book = getItem(position).getName();
        String username = getItem(position).getUsername();
        String status = getItem(position).getStatus();

        //instantiate contents of layout xml into
        //corresponding view objects
        //takes inflater from MainActivity
        //inflates to proper xml
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        //get corresponding TextViews by id
        TextView textViewBook = (TextView) convertView.findViewById(R.id.book_name);
        TextView textViewUsername = (TextView) convertView.findViewById(R.id.username);
        TextView textViewStatus = (TextView) convertView.findViewById(R.id.book_status);

        //set the TextViews
        textViewBook.setText(book);
        textViewUsername.setText(username);
        textViewStatus.setText(status);

        //return the view
        return convertView;

    }
}
