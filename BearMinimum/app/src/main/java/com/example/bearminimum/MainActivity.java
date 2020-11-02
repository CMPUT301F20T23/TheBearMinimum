package com.example.bearminimum;

import android.os.Bundle;

<<<<<<< Updated upstream
import com.google.android.material.bottomnavigation.BottomNavigationView;

=======
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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
>>>>>>> Stashed changes
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
<<<<<<< Updated upstream

public class MainActivity extends AppCompatActivity {
=======
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, NavigationListAdapter.OnBookClickListener {
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
    private String selectedFilter = "all";
    private ArrayList<Book> bookDataList;
    private ArrayList<Book> stateFilter;
    private Spinner filterSpinner;

    /******************Collection/ filter status END*********************/
    /******************ISBN BOOK SEARCH START****************************/
    public EditText ISBNNum;
    public Button ISBNSearchButton;
    public String isbnValue;
    /******************ISBN BOOK SEARCH END****************************/
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /******************ISBN BOOK SEARCH START****************************/
        /******************ISBN BOOK SEARCH END****************************/
        super.onCreate(savedInstanceState);
<<<<<<< Updated upstream
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

=======
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
        getSupportActionBar().setTitle("unRead");

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        stateFilter = new ArrayList<Book>();
        bookDataList = new ArrayList<>();
        filterSpinner = findViewById(R.id.filter_spinner);

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

        Snackbar.make(findViewById(R.id.drawer_layout), "Signed in as " + currentUser.getDisplayName(),Snackbar.LENGTH_LONG).show();


        //connect to RecyclerView
        //UI
        RecyclerView rvBooks = (RecyclerView) findViewById(R.id.list_of_books);
        //initialize books

        //create adapter passing in user data
        adapter = new NavigationListAdapter(stateFilter, this);
        //set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvBooks.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvBooks.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getBaseContext().getResources().getDrawable(R.drawable.item_divider));
        rvBooks.addItemDecoration(dividerItemDecoration);
        //attach adapter to recyclerview to populate
        rvBooks.setAdapter(adapter);

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

                        Book book = new Book(BookName, author,owner,Borrower,Description,isbn,status, BookId);
                        Log.i("Test",status);

                        bookDataList.add(book); // Adding the cities and provinces from FireStore
                    }
                }
                filterList(selectedFilter);
            }
        });
        /******************ISBN BOOK SEARCH START****************************/
        ISBNSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isbnValue = ISBNNum.getText().toString(); // get the isbn on click
                Log.i("Riky",isbnValue);
                if (isbnValue.length() < 10) {
                    Toast.makeText(MainActivity.this, "ISBN must be 10 or 13 digits", Toast.LENGTH_SHORT).show();
                } else {


                    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbnValue;
                    //Do http request
                    Log.i("Riky",url);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                            url,null, // here
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {


                                    String[] result = jsonParser(response);
                                    if (result[0] != null) {

                                        Intent intent = new Intent(MainActivity.this,ISBNaddBook.class);

                                        intent.putExtra("name",result[0]);
                                        intent.putExtra("des",result[1]);
                                        intent.putExtra("author",result[2]);
                                        intent.putExtra("isbn",isbnValue);
                                        startActivityForResult(intent,0);

                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    AppController.getInstance(MainActivity.this).addToRequestQueue(request);
                }
            }
        });
        /******************ISBN BOOK SEARCH END****************************/
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

        } else if (item.getItemId() == R.id.nav_incoming_requests) {
            Intent intent = new Intent(this, IncomingReqs.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_outgoing_requests) {

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
    public void onBookClick(int position) {

    }
    /******************ISBN BOOK SEARCH START****************************/
    public String[] jsonParser(JSONObject response) {
        String[] result = new String[5]; // volume information holder
        try {
            String totalItems = response.optString("totalItems");
            if (totalItems.equalsIgnoreCase("0")) {

                Toast.makeText(MainActivity.this, "Invalid ISBN", Toast.LENGTH_LONG).show();
            } else {
                JSONArray jsonArray = response.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject items = jsonArray.getJSONObject(i);

                    // get title info
                    String title = items.getJSONObject("volumeInfo").optString("title");
                    String subtitle = items.getJSONObject("volumeInfo").optString("subtitle");
                    result[0] = title + " : " + subtitle;

                    // get author info
                    result[1] = items.getJSONObject("volumeInfo").optString("description");

                    // get category and page count info
                    result[2] = items.getJSONObject("volumeInfo").optString("authors");
                    result[3] = items.getJSONObject("volumeInfo").optString("identifier");

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    /******************ISBN BOOK SEARCH END****************************/
>>>>>>> Stashed changes
}