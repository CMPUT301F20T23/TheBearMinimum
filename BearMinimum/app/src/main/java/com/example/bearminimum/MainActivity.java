package com.example.bearminimum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    //custom adapter
    NavigationListAdapter adapter;
    /******************Collection/ filter status START*********************/
    public String selectedFilter = "all";
    public static ArrayAdapter<Book> bookAdapter;
    public static ArrayList<Book> bookDataList;


    /******************Collection/ filter status END*********************/

    @NonNull
    public static Intent createIntent(@NonNull Context context, @Nullable IdpResponse response) {
        return new Intent().setClass(context, MainActivity.class)
                .putExtra(ExtraConstants.IDP_RESPONSE, response);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_main);

        //
        //adapter = new NavigationListAdapter(this, R.layout.navigation_list, bookList);
        //

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(AuthPage.createIntent(this));
            finish();
            return;
        }

        Snackbar.make(navView, "Signed in as " + currentUser.getDisplayName(),Snackbar.LENGTH_LONG).show();
    }



    /******************Collection/ filter status START*********************/
    // here we get all information from database and create a bookList for current user

    private void FilterList(String Clicked){
        final FirebaseUser CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final CollectionReference collectionReference = db.collection("books");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                bookDataList.clear();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    String owner = (String) doc.getData().get("owner");
                    if (owner == CurrentUser.getDisplayName() ){
                        String BookId = doc.getId();
                        String BookName = (String) doc.getData().get("title");
                        String author = (String) doc.getData().get("author");
                        String isbn = (String) doc.getData().get("isbn");
                        String Borrower = (String) doc.getData().get("borrower");
                        String status = (String) doc.getData().get("status");
                        String Description = (String) doc.getData().get("description");

                        Book book = new Book(BookName, author,owner,Borrower,Description,isbn,status);
                        book.setBorrower(Borrower);
                        Log.i("Test",status);

                        bookDataList.add(book); // Adding the cities and provinces from FireStore
                    }


                }
                bookAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched

            }
        });

        selectedFilter = Clicked;
        ArrayList<Book> stateFilter = new ArrayList<Book>();

        for(Book book:bookDataList){
            if (Clicked == "UNBORROWED"){
                if (book.getStatus() == "UNBorrowed"){
                    stateFilter.add(book);
                }
            }
            else if (Clicked == "BORROWED"){
                if (book.getStatus() == "Borrowed"){
                    stateFilter.add(book);
                }
            }

        }
        //**********************The adapter haven't set up and need the layout to set up
        //bookAdapter = new BookAdapter(this, stateFilter);

        //BList.setAdapter(bookAdapter);

    }
    public void all_button(View view){
        //*********************Wait for the adapter(the layout to link to )
        //bookAdapter = new BookAdapter(this, bookDataList);

        //BList.setAdapter(bookAdapter);

    }

    //Here we wait for the booton on the collection layout
    public void UB_button(View view){
        FilterList("UNBORROWED");

    }
    public void B_button(View view){
        FilterList("BORROWED");
    }

    /******************Collection/ filter status END*********************/
    
}