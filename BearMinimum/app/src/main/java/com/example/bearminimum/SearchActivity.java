package com.example.bearminimum;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private ArrayList<User> filteredUserList;
    private ArrayList<Book> filteredBookList;
    private BookSearchAdapter bookAdapter;
    private UserSearchAdapter userAdapter;

    //firebase
    private FirebaseFirestore db;
    private CollectionReference booksRef;
    private CollectionReference usersRef;

    private boolean searchingBooks = true;
    private RecyclerView recyclerView;

    private String filterStr = "";

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
                filterStr = editable.toString();
                if (searchingBooks)
                    filterBooks(filterStr);
                else
                    filterUsers(filterStr);
            }
        });

        Button books = findViewById(R.id.search_books);
        Button users = findViewById(R.id.search_users);
        GradientDrawable booksBtnBG = (GradientDrawable) books.getBackground().mutate();
        GradientDrawable usersBtnBG = (GradientDrawable) users.getBackground().mutate();
        booksBtnBG.setColor(getResources().getColor(R.color.white));
        books.setTextColor(getResources().getColor(R.color.blue));

        books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggle selection
                if (!searchingBooks) {
                    booksBtnBG.setColor(getResources().getColor(R.color.white));
                    books.setTextColor(getResources().getColor(R.color.blue));
                    //deselect borrowed button
                    usersBtnBG.setColor(getResources().getColor(R.color.blue));
                    users.setTextColor(getResources().getColor(R.color.white));
                }
                searchingBooks = true;
                recyclerView.setAdapter(bookAdapter);
                searchTextBar.setText("");

            }
        });

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggle selection
                if (searchingBooks) {
                    usersBtnBG.setColor(getResources().getColor(R.color.white));
                    users.setTextColor(getResources().getColor(R.color.blue));
                    //deselect borrowed button
                    booksBtnBG.setColor(getResources().getColor(R.color.blue));
                    books.setTextColor(getResources().getColor(R.color.white));
                }
                searchingBooks = false;
                recyclerView.setAdapter(userAdapter);
                searchTextBar.setText("");
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
        filteredBookList.clear();

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
        filteredUserList.clear();

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
        bookAdapter = new BookSearchAdapter(filteredBookList, this);

        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * Sets up a user adapter for the RecyclerView
     */
    private void setUpUserAdapter() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        userAdapter = new UserSearchAdapter(filteredUserList, this);

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
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        bookList.clear();
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot document : value.getDocuments()) {
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
                                String owner_scan=(String) document.get("owner_scan") ;
                                String borrower_scan=(String) document.get("borrower_scan") ;
                                String lat = (String) document.getString("latitude");
                                String longitude = (String) document.getString("longitude");
                                bookList.add(new Book(title, author, owner, borrower, description, isbn, status, bid, lat, longitude, owner_scan, borrower_scan));

                                Log.d(TAG, document.getId() + " :a book");
                            }
                        } else {
                            Log.d(TAG, "error getting book documents");
                        }
                        filterBooks(filterStr);
                        bookAdapter.notifyDataSetChanged();
                    }
                });
        filteredBookList = (ArrayList<Book>) bookList.clone();
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
        filteredUserList = (ArrayList<User>) userList.clone();
    }

    @Override
    public void onResultClick(int position) {
        Book book = filteredBookList.get(position);
        Intent intent = ViewBookActivity.createIntent(book, this, false);
        startActivity(intent);
    }

    @Override
    public void onUserClick(int position) {
        User user = filteredUserList.get(position);
        Intent intent = ProfileActivity.createIntent(user.getUid(), getBaseContext(), false);
        startActivity(intent);
    }
}
