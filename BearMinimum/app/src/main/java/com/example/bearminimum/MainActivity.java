package com.example.bearminimum;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

<<<<<<< Updated upstream
=======
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
            Intent intent = new Intent(this, AddBookActivity.class);
            startActivity(intent);

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
>>>>>>> Stashed changes
}