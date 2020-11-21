package com.example.bearminimum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class AcceptedIncomingReqs extends AppCompatActivity implements NavigationListAdapter.OnBookClickListener{

    private RecyclerView recyclerView;
    private NavigationListAdapter adapter;
    private ArrayList<Book> bookData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_incoming_reqs);

        bookData = new ArrayList<>();
        recyclerView = findViewById(R.id.accepted_incoming_recycler);
        adapter = new NavigationListAdapter(bookData,this);
    }

    @Override
    public void onBookClick(int position, String owner) {
        //open either location selector or previously selected location
    }
}