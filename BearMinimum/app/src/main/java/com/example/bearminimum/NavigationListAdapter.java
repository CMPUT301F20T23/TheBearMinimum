package com.example.bearminimum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//basic adapter extending RecyclerView.Adapter
//custom ViewHolder to access our views
public class NavigationListAdapter extends
        RecyclerView.Adapter<NavigationListAdapter.ViewHolder> {

    //direct reference to each view within a data item
    public class ViewHolder extends RecyclerView.ViewHolder {

        //member variable for views rendered as rows
        public TextView bookTextView;
        public TextView userNameTextView;
        public TextView statusTextView;

        //constructor that accepts new row
        //view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);

            bookTextView = (TextView) itemView.findViewById(R.id.book_name);
            userNameTextView = (TextView) itemView.findViewById(R.id.username);
            statusTextView = (TextView) itemView.findViewById(R.id.book_status);
        }
    }

    //member variable for book objects
    private List<Book> mBooks;

    //pass in the book array to the constructor
    public NavigationListAdapter(List<Book> books) {
        mBooks = books;
    }

    //adapter requires three primary methods
    //onCreateViewHolder to inflate and create the holder
    //onBindViewHolder to set the view attributes based on data
    //getItemCount to determine number of items

    @Override
    public NavigationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the custom layout
        View contactView = inflater.inflate(R.layout.navigation_list, parent, false);

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    //populate data into the item through holder
    @Override
    public void onBindViewHolder(NavigationListAdapter.ViewHolder holder, int position) {

        //get data based on position
        Book book = mBooks.get(position);

        //set item views based on views and data
        TextView textView1 = holder.bookTextView;
        textView1.setText(book.getTitle());
        TextView textView2 = holder.userNameTextView;
        textView2.setText(book.getOwner());
        TextView textView3 = holder.statusTextView;
        textView3.setText(book.getStatus());
    }

    //return total count of items in the list
    @Override
    public int getItemCount() {

        return mBooks.size();
    }






}





/* comment out for now
/ changed to recycler view

//assumes Book class exists
public class NavigationListAdapter extends ArrayAdapter<Book> {

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
        String book = getItem(position).getTitle();
        String username = getItem(position).getOwner();
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
*/