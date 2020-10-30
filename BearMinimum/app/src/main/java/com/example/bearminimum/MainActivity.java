package com.example.bearminimum;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;


import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bookCollection = db.collection("books");
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    //custom adapter
    private NavigationListAdapter adapter;

    //design components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageButton menuButton;


    /******************Collection/ filter status START*********************/
    private String selectedFilter = "ALL";
    private ArrayList<Book> bookDataList;
    private ArrayList<Book> stateFilter;
    private Button allButton;
    private Button UBButton;
    private Button BButton;

    /******************Collection/ filter status END*********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_main);


        if (currentUser == null) {
            startActivity(AuthPage.createIntent(this));
            finish();
            return;
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        stateFilter = new ArrayList<Book>();
        bookDataList = new ArrayList<>();
        allButton = findViewById(R.id.all_books);
        UBButton = findViewById(R.id.unborrowed_books);
        BButton = findViewById(R.id.borrowed_books);

        Snackbar.make(findViewById(R.id.drawer_layout), "Signed in as " + currentUser.getDisplayName(),Snackbar.LENGTH_LONG).show();

        menuButton = findViewById(R.id.navigation);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isOpen())
                    drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        //connect to RecyclerView
        //UI
        RecyclerView rvBooks = (RecyclerView) findViewById(R.id.list_of_books);
        //initialize books
        //need to set books to list from firebase?

        //here

        //create adapter passing in user data
        adapter = new NavigationListAdapter(stateFilter);
        //attach adapter to recyclerview to populate
        rvBooks.setAdapter(adapter);
        //set layout manager to position the items
        rvBooks.setLayoutManager(new LinearLayoutManager(this));

        //to add to existing list
        //make change to data source directly and notify adapter of changes
        //books.addALL(existing list);
        //books.add(0, new Book(x,y,z));
        //adapter.notifyItemInserted(0);
        //need to explicitly inform adapter of event
        //do no rely on notifyDataSetChanged() - more granular should be used
        //ie
        //int currentSize = adapter.getItemCount();
        //ArrayList<Book> newBooks = list;
        //books.addAll(newItems);
        //adapter.notifyItemRangeInserted(curSize, newItems.size());
        //like that

        bookCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                bookDataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    String owner = (String) doc.getData().get("owner");
                    if (owner.equals(currentUser.getUid())){
                        String BookId = doc.getId();
                        String BookName = (String) doc.getData().get("title");
                        String author = (String) doc.getData().get("author");
                        String isbn = (String) doc.getData().get("isbn");
                        String Borrower = (String) doc.getData().get("borrower");
                        String status = (String) doc.getData().get("status");
                        String Description = (String) doc.getData().get("description");
                        String bid = (String) doc.getData().get("bookid");

                        Book book = new Book(BookName, author,owner,Borrower,Description,isbn,status, bid);
                        book.setBorrower(Borrower);
                        Log.i("Test",status);

                        bookDataList.add(book); // Adding the cities and provinces from FireStore
                    }
                }
                filterList(selectedFilter);
            }
        });

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterList("ALL");
            }
        });
        UBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterList("UNBORROWED");
            }
        });
        BButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterList("BORROWED");
            }
        });
    }


    //delete book
    //TODO
    // call this under deletebutton.setOnClickListener
    public void deleteBook(Book selectedBook) {

        String bookID;

        if (selectedBook != null) {

            //get bookid
            bookID = selectedBook.getBid();

            db.collection("books").document(bookID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "book successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting book", e);
                        }
                    });
        }
    }

    //delete book photo
    //TODO
    // call this under edit book
    public void deleteBookPhoto(Book selectedBook) {

        //get bookid
        String bookID = selectedBook.getBid();

        //get storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a reference to the file to delete
        StorageReference ref = storage.getReference().child("book_cover_images/" + bookID);

        // Delete the file
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "deleted photo corresponding to" + bookID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "couldn't delete photo");
            }
        });
    }

    /******************Collection/ filter status START*********************/
    // here we get all information from database and create a bookList for current user

    private void filterList(String Clicked){
        selectedFilter = Clicked;
        stateFilter.clear();

        for(Book book:bookDataList){
            //Log.d("MyDebug", "status " + book.getStatus());
            if (Clicked.equals("UNBORROWED")){
                if (book.getStatus().equals("unborrowed")){
                    stateFilter.add(book);
                }
            }
            else if (Clicked.equals("BORROWED")){
                if (book.getStatus().equals("borrowed")){
                    stateFilter.add(book);
                }
            } else if (Clicked.equals("ALL")) {
                stateFilter.add(book);
            }

        }
        adapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
    }


    /******************Collection/ filter status END*********************/

    //implement for back pressed with menu open
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getBaseContext(), AuthPage.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_add_book) {

        } else if (item.getItemId() == R.id.nav_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.nav_incoming_requests) {

        } else if (item.getItemId() == R.id.nav_outgoing_requests) {

        }
        return true;
    }
}