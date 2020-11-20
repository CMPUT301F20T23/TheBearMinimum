package com.example.bearminimum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * This activity displays books that have been requested by the current user, and requests can be withdrawn by viewing the book
 */
public class OutgoingReqsActivity extends AppCompatActivity implements NavigationListAdapter.OnBookClickListener {

    RecyclerView bookList;
    ArrayList<Book> bookData;
    NavigationListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_reqs);

        //initialize variables
        bookData = new ArrayList<>();
        initRecycler();

        //add realtime listener for query. the query returns books that the user has requested
        FirebaseFirestore.getInstance().collection("books")
                .whereArrayContains("requests", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        //update requested books list
                        if (value != null) {
                            bookData.clear();
                            if (!value.isEmpty()) {
                                for (QueryDocumentSnapshot doc : value) {
                                    Map data = doc.getData();
                                    String title = (String) data.get("title");
                                    String author = (String) data.get("author");
                                    String bid = (String) data.get("bookid");
                                    String desc = (String) data.get("description");
                                    String isbn = (String) data.get("isbn");
                                    String status = (String) data.get("status");
                                    String borrower = (String) data.get("borrower");
                                    String owner = (String) data.get("owner");
                                    bookData.add(new Book(title, author, owner, borrower, desc, isbn, status, bid));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    /**
     * setup the recycler adapter for use
     */
    private void initRecycler() {
        bookList = findViewById(R.id.outgoing_reqs_list);
        bookList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(bookList.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getBaseContext().getResources().getDrawable(R.drawable.item_divider));
        bookList.addItemDecoration(dividerItemDecoration);
        adapter = new NavigationListAdapter(bookData, this);
        bookList.setAdapter(adapter);
    }

    @Override
    public void onBookClick(int position, String owner) {
        Intent intent = ViewBookActivity.createIntent(bookData.get(position), this, false);
        startActivity(intent);
    }
}