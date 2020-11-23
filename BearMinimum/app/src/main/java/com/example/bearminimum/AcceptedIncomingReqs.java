package com.example.bearminimum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AcceptedIncomingReqs extends AppCompatActivity implements AcceptedIncomingAdapter.OnBookClickListener{

    private RecyclerView recyclerView;
    private AcceptedIncomingAdapter adapter;
    private ArrayList<Book> bookData;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference booksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_incoming_reqs);

        bookData = new ArrayList<>();
        initAdapter();

        booksRef = FirebaseFirestore.getInstance().collection("books");
        Query query = booksRef.whereEqualTo("owner", user.getUid()).whereEqualTo("status", "accepted");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                bookData.clear();
                if (!value.isEmpty()) {
                    for (QueryDocumentSnapshot doc : value) {
                        Map data = doc.getData();
                        Log.d("MyDebug", "requests exist");
                        String title = (String) data.get("title");
                        String author = (String) data.get("author");
                        String bid = (String) data.get("bookid");
                        String desc = (String) data.get("description");
                        String isbn = (String) data.get("isbn");
                        String status = (String) data.get("status");
                        String borrower = (String) data.get("borrower");
                        String lat = (String) data.get("latitude");
                        Log.d("MyDebug", "lat is: " + lat);
                        String longitude = (String) data.get("longitude");
                        bookData.add(new Book(title, author, user.getUid(), borrower, desc, isbn, status, bid, lat, longitude));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initAdapter() {
        recyclerView = findViewById(R.id.accepted_incoming_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new AcceptedIncomingAdapter(bookData, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBookClick(int position, String owner) {
        //open either location selector or previously selected location
        Book book = bookData.get(position);
        if (!book.getLatitude().equals("")) {
            //launch location selector
        } else {
            //launch location viewer
        }
    }
}