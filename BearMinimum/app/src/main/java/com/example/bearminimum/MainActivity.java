package com.example.bearminimum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    @NonNull
    public static Intent createIntent(@NonNull Context context, @Nullable IdpResponse response) {
        return new Intent().setClass(context, MainActivity.class)
                .putExtra(ExtraConstants.IDP_RESPONSE, response);
    }


    //array for books
    //for RecyclerView
    ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_main);

        //connect to RecyclerView
        //UI
        RecyclerView rvBooks = (RecyclerView) findViewById(R.id.list_of_books);
        //initialize books
        //need to set books to list from firebase?

        //here

        //create adapter passing in user data
        NavigationListAdapter adapter = new NavigationListAdapter(books);
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
    
}