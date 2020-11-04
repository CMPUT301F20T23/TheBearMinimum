package com.example.bearminimum;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * SearchActivity
 *
 * Allows user to search through available books to borrow, using keywords
 * Shows results in list
 *
 * Nov 2, 2020
 */

//referenced: https://codinginflow.com/tutorials/android/searchview-recyclerview

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SEARCH_ACTIVITY";
    //adapter
    private ArrayList<Book> bookList;
    private BookSearchAdapter adapter;

    //firebase
    private FirebaseFirestore db;
    private CollectionReference booksRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        getBooks();
        setUpAdapter();

        //using the editText as a search bar
        EditText searchTextBar = findViewById(R.id.search_editText);
        searchTextBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    /**
     * Takes in a keyword and searches through book
     * titles, author, ISBN, descriptions fields for
     * any books that have a match with the keyword
     *
     * @param text
     * The keyword to search for
     */

    private void filter(String text) {
        ArrayList<Book> filteredList = new ArrayList<>();

        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(text.toLowerCase()) ||
                    book.getISBN().contains(text) ||
                    book.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(book);
            }
        }


        adapter.filteredList(filteredList);
    }

    /**
     * Sets up an adapter for the RecyclerView
     */

    private void setUpAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new BookSearchAdapter(bookList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Queries for all books that don't belong to the user,
     * and have status as 'available' or 'requested'
     * Creates a list of Book objects of all books found
     */

    private void getBooks() {

        bookList = new ArrayList<>();

        //firebase
        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");

        //get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentUser.getUid();

        //query for books that are:
        // - not owned by user
        // - available/requested but not accepted/borrowed
        booksRef.whereNotEqualTo("owner", userID)
                .whereIn("status", Arrays.asList("available", "requested"))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "got books");
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //create book object from each document
                                //then append to list
                                String title = document.getString("title");
                                String author = document.getString("author");
                                String owner = document.getString("owner");
                                String borrower = document.getString("borrower");
                                String description = document.getString("description");
                                String isbn = document.getString("isbn");
                                String status = document.getString("status");
                                String bid = document.getString("bookid");

                                bookList.add(new Book(title, author, owner, borrower, description, isbn, status, bid));

                                Log.d(TAG, document.getId() + " :a book");
                            }
                        } else {
                            Log.d(TAG, "error getting book documents");

                        }
                        filter("");
                    }
        });

    }

}
