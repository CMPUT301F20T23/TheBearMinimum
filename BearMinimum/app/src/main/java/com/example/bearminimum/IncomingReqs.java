package com.example.bearminimum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

/**
 * IncomingReqs
 *
 * This activity displays all the of the current user's books that have requests on them
 *
 * Nov. 6, 2020
 */
public class IncomingReqs extends AppCompatActivity implements NavigationListAdapter.OnBookClickListener {
    private NavigationListAdapter adapter;
    private ArrayList<Book> bookData;
    private RecyclerView bookRecyclerView;

    private CollectionReference booksRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_reqs);

        //setup recycler and its data
        bookData = new ArrayList<>();
        bookRecyclerView = findViewById(R.id.books_with_reqs);
        initRecycler();

        booksRef = FirebaseFirestore.getInstance().collection("books");

        //listen for owned books that have the requested status
        Query query = booksRef.whereEqualTo("owner", user.getUid()).whereEqualTo("status", "requested");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Log.d("MyDebug", "requested books data changed");
                bookData.clear();
                if (value.isEmpty())
                    return;
                for (QueryDocumentSnapshot doc : value) {
                    List<String> requesters = (List<String>) doc.getData().get("requests");
                    Map data = doc.getData();
                    if (requesters.size() > 0) {
                        Log.d("MyDebug", "requests exist");
                        String title = (String) data.get("title");
                        String author = (String) data.get("author");
                        String bid = (String) data.get("bookid");
                        String desc = (String) data.get("description");
                        String isbn = (String) data.get("isbn");
                        String status = (String) data.get("status");
                        String borrower = (String) data.get("borrower");
                        String owner_scan=(String) data.get("owner_scan") ;
                        String borrower_scan=(String) data.get("borrower_scan") ;

                        bookData.add(new Book(title,author,user.getUid(),borrower,desc,isbn,status,owner_scan,borrower_scan,bid));
                    } else
                        booksRef.document((String) data.get("bookid")).update("status", "available");
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This initialized the array adapter with a linear layout manager and sets the view adapter
     */
    private void initRecycler() {
        bookRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new NavigationListAdapter(bookData, this);
        bookRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBookClick(int position) {
        //when a book is selected, show the individual requests for it
        Intent intent = new Intent(this, HandleIncomingReqsActivity.class);
        intent.putExtra("bookid", bookData.get(position).getBid());
        startActivity(intent);
    }
}