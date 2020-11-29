package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Map;

public class AcceptedIncomingReqs extends AppCompatActivity implements AcceptedIncomingAdapter.OnBookClickListener{

    private RecyclerView recyclerView;
    private AcceptedIncomingAdapter adapter;
    private ArrayList<Book> bookData;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference booksRef;
    private FirebaseFirestore db;

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
                        String owner_scan=(String)doc.getData().get("owner_scan") ;
                        String borrower_scan=(String)doc.getData().get("borrower_scan") ;
                        Log.d("MyDebug", "lat is: " + lat);
                        String longitude = (String) data.get("longitude");
                        bookData.add(new Book(title, author, user.getUid(), borrower, desc, isbn, status, bid, lat, longitude, owner_scan, borrower_scan));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent D) {
        super.onActivityResult(requestCode, resultCode, D);

        if (resultCode == 4){
            db = FirebaseFirestore.getInstance();
            Log.i("Riky","Tester6666");
            String isbn_num = D.getStringExtra("isbn_number");
            db.collection("books").whereEqualTo("owner", user.getUid())
                    .whereEqualTo("isbn", isbn_num)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    doc.getReference().update("status", "available");
                                    Log.d("AcceptedIncomingReqs", "status changed to available");
                                }

                            }
                        }
                    });
            
        }
    }


    private void initAdapter() {
        recyclerView = findViewById(R.id.accepted_incoming_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getBaseContext().getResources().getDrawable(R.drawable.item_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new AcceptedIncomingAdapter(bookData, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBookClick(int position, String owner) {
        //open either location selector or previously selected location
        Book book = bookData.get(position);
        //launch location selector
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("bookid", book.getBid());
        intent.putExtra("borrower", book.getBorrower());
        intent.putExtra("latitude", book.getLatitude());
        intent.putExtra("longitude", book.getLongitude());
        startActivity(intent);
    }



}