package com.example.bearminimum;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class SearchActivity extends AppCompatActivity implements BookSearchAdapter.OnResultClickListener, UserSearchAdapter.OnUserClickListener {

    private static final String TAG = "SEARCH_ACTIVITY";
    //adapter
    private ArrayList<Book> bookList;
    private ArrayList<User> userList;
    private BookSearchAdapter bookAdapter;
    private UserSearchAdapter userAdapter;

    //firebase
    private FirebaseFirestore db;
    private CollectionReference booksRef;
    private CollectionReference usersRef;

    private boolean searchingBooks = true;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        getBooks();
        getUsers();
        setUpBookAdapter();
        setUpUserAdapter();

        recyclerView.setAdapter(bookAdapter);

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
                if (searchingBooks)
                    filterBooks(editable.toString());
                else
                    filterUsers(editable.toString());
            }
        });

        Button books = findViewById(R.id.search_books);
        books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchingBooks = true;
                recyclerView.setAdapter(bookAdapter);
            }
        });
        Button users = findViewById(R.id.search_users);
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchingBooks = false;
                recyclerView.setAdapter(userAdapter);
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

    private void filterBooks(String text) {
        ArrayList<Book> filteredBookList = new ArrayList<>();

        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(text.toLowerCase()) ||
                    book.getISBN().contains(text) ||
                    book.getDescription().toLowerCase().contains(text.toLowerCase())) {
                filteredBookList.add(book);
            }
        }


        bookAdapter.filteredList(filteredBookList);
    }

    /**
     * Takes in a keyword and searches for usernames that contain it
     * @param text
     * The keyword to search for
     */
    private void filterUsers(String text) {
        ArrayList<User> filteredUserList = new ArrayList<>();

        for (User user : userList) {
            if (user.getUsername().toLowerCase().contains(text.toLowerCase())) {
                filteredUserList.add(user);
            }
        }

        userAdapter.filteredList(filteredUserList);
    }

    /**
     * Sets up a book adapter for the RecyclerView
     */

    private void setUpBookAdapter() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        bookAdapter = new BookSearchAdapter(bookList, this);

        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Sets up a user adapter for the RecyclerView
     */
    private void setUpUserAdapter() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserSearchAdapter(userList, this);

        recyclerView.setLayoutManager(layoutManager);
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
                        filterBooks("");
                    }
        });

    }

    /**
     * Queries for all users that are not the current user
     */
    private void getUsers() {
        userList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        //get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = currentUser.getUid();

        //query for users that are not the current user
        usersRef.whereNotEqualTo("uid", userID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String email = document.getString("email");
                        String phonenumber = document.getString("phonenumber");
                        String uid = document.getString("uid");
                        String username = document.getString("username");
                        userList.add(new User(email,phonenumber,uid,username));
                    }
                }
                filterUsers("");
            }
        });
    }

    @Override
    public void onResultClick(int position) {
        Book book = bookList.get(position);
        Intent intent = ViewBookActivity.createIntent(book, this, false);
        startActivity(intent);
    }

    @Override
    public void onUserClick(int position) {
        User user = userList.get(position);
        Intent intent = ProfileActivity.createIntent(user.getUid(), getBaseContext(), false);
        startActivity(intent);
    }
}
