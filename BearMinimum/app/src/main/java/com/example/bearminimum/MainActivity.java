package com.example.bearminimum;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * MainActivity
 *
 * Main page of the app once user has signed in.
 *
 * Nov. 3, 2020
 */


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, NavigationListAdapter.OnBookClickListener {
    //firebase
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference bookCollection = db.collection("books");
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    //custom adapter
    private NavigationListAdapter collectionAdapter;
    private NavigationListAdapter borrowedAdapter;

    //design components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ImageButton menuButton;

    //buttons
    private Button borrowedBtn;
    private Button collectionBtn;
    private boolean isCollectionBtn = true;

    /******************Collection/ filter status START*********************/
    private String selectedFilter = "all";
    private ArrayList<Book> bookDataList;
    private ArrayList<Book> borrowedBookData;
    private ArrayList<Book> stateFilter;
    private Spinner filterSpinner;

    /******************Collection/ filter status END*********************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("unRead");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        stateFilter = new ArrayList<Book>();
        bookDataList = new ArrayList<>();
        borrowedBookData = new ArrayList<Book>();
        filterSpinner = findViewById(R.id.filter_spinner);
        //connect to RecyclerView
        //UI
        RecyclerView rvBooks = findViewById(R.id.list_of_books);

        filterSpinner.setOnItemSelectedListener(this);
        List<String> filters = new ArrayList<>();
        filters.add("all");
        filters.add("available");
        filters.add("requested");
        filters.add("accepted");
        filters.add("borrowed");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list, filters);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        Snackbar sb = Snackbar.make(findViewById(R.id.drawer_layout), "signed in as " + currentUser.getDisplayName(),Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
        sb.show();

        collectionBtn = findViewById(R.id.collection);
        borrowedBtn = findViewById(R.id.borrowed);

        GradientDrawable collectionBtnBG = (GradientDrawable) collectionBtn.getBackground().mutate();
        GradientDrawable borrowedBtnBG = (GradientDrawable) borrowedBtn.getBackground().mutate();
        collectionBtnBG.setColor(getResources().getColor(R.color.white));
        collectionBtn.setTextColor(getResources().getColor(R.color.blue));
        collectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select button
                if (!isCollectionBtn) {
                    collectionBtnBG.setColor(getResources().getColor(R.color.white));
                    collectionBtn.setTextColor(getResources().getColor(R.color.blue));
                    isCollectionBtn = true;
                    rvBooks.setAdapter(collectionAdapter);
                    //deselect borrowed button
                    borrowedBtnBG.setColor(getResources().getColor(R.color.blue));
                    borrowedBtn.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        borrowedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //select button
                if (isCollectionBtn) {
                    borrowedBtnBG.setColor(getResources().getColor(R.color.white));
                    borrowedBtn.setTextColor(getResources().getColor(R.color.blue));
                    isCollectionBtn = false;
                    rvBooks.setAdapter(borrowedAdapter);
                    //deslect collection button
                    collectionBtnBG.setColor(getResources().getColor(R.color.blue));
                    collectionBtn.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        //initialize books

        //create adapter passing in user data
        collectionAdapter = new NavigationListAdapter(stateFilter, this);
        borrowedAdapter = new NavigationListAdapter(borrowedBookData, this);
        initRecycler(rvBooks);
        //attach adapter to recyclerview to populate
        rvBooks.setAdapter(collectionAdapter);

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
                borrowedBookData.clear();
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
                        String lat = (String) doc.getData().get("latitude");
                        String longitude = (String) doc.getData().get("longitude");
                        Book book = new Book(BookName, author,owner,Borrower,Description,isbn,status, BookId, lat, longitude);
                        Log.i("Test",status);

                        bookDataList.add(book); // Adding the cities and provinces from FireStore
                    }
                    String borrower = (String) doc.getData().get("borrower");
                    if (borrower.equals(currentUser.getUid())) {
                        String BookId = doc.getId();
                        String BookName = (String) doc.getData().get("title");
                        String author = (String) doc.getData().get("author");
                        String isbn = (String) doc.getData().get("isbn");
                        String Borrower = (String) doc.getData().get("borrower");
                        String status = (String) doc.getData().get("status");
                        String Description = (String) doc.getData().get("description");
                        String lat = (String) doc.getData().get("latitude");
                        String longitude = (String) doc.getData().get("longitude");
                        Book book = new Book(BookName, author,owner,Borrower,Description,isbn,status, BookId, lat, longitude);

                        borrowedBookData.add(book); // Adding the cities and provinces from FireStore
                    }
                }
                filterList(selectedFilter);
                borrowedAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initRecycler(RecyclerView books) {
        //set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        books.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(books.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getBaseContext().getResources().getDrawable(R.drawable.item_divider));
        books.addItemDecoration(dividerItemDecoration);
    }


    /******************Collection/ filter status START*********************/
    // here we get all information from database and create a bookList for current user

    /**
     * Filters a list of Book objects based on the selected filter
     * to display in the RecyclerView list.
     *
     * @param Clicked  The current String type filter selected from the spinner.
     *                 Obtained from function OnItemSelected.
     */

    private void filterList(String Clicked){
        selectedFilter = Clicked;
        stateFilter.clear();

        for(Book book:bookDataList){
            //Log.d("MyDebug", "status " + book.getStatus());
            if (Clicked.equals("available")){
                if (book.getStatus().equals("available")){
                    stateFilter.add(book);
                }
            } else if (Clicked.equals("requested")){
                if (book.getStatus().equals("requested")){
                    stateFilter.add(book);
                }
            } else if (Clicked.equals("accepted")) {
                if (book.getStatus().equals("accepted")){
                    stateFilter.add(book);
                }
            } else if (Clicked.equals("borrowed")) {
                if (book.getStatus().equals("borrowed")){
                    stateFilter.add(book);
                }
            } else if (Clicked.equals("all")) {
                stateFilter.add(book);
            }

        }
        collectionAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched
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


    /**
     * The side drawer menu that contains different menu items.
     * Clicking on a menu item will navigate the user to the
     * perspective activity for that task.
     *
     * @param   item  the menu item selected from the drawer
     * @return        a boolean that indicates the drawer has closed
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_profile) {
            Intent intent = ProfileActivity.createIntent(currentUser.getUid(), getBaseContext(), true);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getBaseContext(), AuthPage.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_add_book) {
            Intent intent = new Intent(this, AddBookActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_managebook) {
            Intent intent = new Intent(this, ManageBook.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_incoming_requests) {
            Intent intent = new Intent(this, IncomingReqs.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_outgoing_requests) {
            Intent intent = new Intent(this, OutgoingReqsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_accepted_incoming_requests) {
            Intent intent = new Intent(this, AcceptedIncomingReqs.class);
        } else if (item.getItemId() == R.id.nav_notifications) {
            Intent intent = new Intent(this, SpecficBookRequest.class);
            startActivity(intent);
        }
        drawerLayout.close();
        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                filterList("all");
                break;
            case 1:
                filterList("available");
                break;
            case 2:
                filterList("requested");
                break;
            case 3:
                filterList("accepted");
                break;
            case 4:
                filterList("borrowed");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onBookClick(int position, String owner) {
        Intent intent;
        Book book;
        if (owner.equals(currentUser.getUid())) {
            book = stateFilter.get(position);
            intent = ViewBookActivity.createIntent(book, this, true);
        } else {
            book = borrowedBookData.get(position);
            intent = ViewBookActivity.createIntent(book,this, false);
        }
        startActivity(intent);
    }
}