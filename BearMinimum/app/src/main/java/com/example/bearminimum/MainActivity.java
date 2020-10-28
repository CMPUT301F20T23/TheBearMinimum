package com.example.bearminimum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.WriteResult;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        adapter = new NavigationListAdapter(this, R.layout.navigation_list, bookDataList);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(AuthPage.createIntent(this));
            finish();
            return;
        }

        Snackbar.make(findViewById(R.id.main_view), "Signed in as " + currentUser.getDisplayName(),Snackbar.LENGTH_LONG).show();
    }


    //delete book
    //TODO
    // call this under deletebutton.setOnClickListener
    public void deleteBook(Book selectedBook) {

        String bookID;

        if (selectedBook != null) {

            //get book ISBN, and user id
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

            /*
            //query for the book (matching owner and isbn)
            CollectionReference booksRef = db.collection("books");
            booksRef.whereEqualTo("bookid", bookID);


            booksRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Log.d("MyDebug", "Selected book does not exist!");
                            return;
                        } else if (task.getResult().size() > 1) {
                            Log.d("MyDebug", "Fatal: database integrity fault");
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            //find the book
                            if (document.getString("ISBN") == ISBN) {
                                Log.d(TAG, document.getId() + " found book");

                                //delete the book
                                db.collection("books").document(document.getId())
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
                    } else {
                        Log.d(TAG, "Error getting books: ", task.getException());
                    }
                }
            });

             */
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
                    if (owner == CurrentUser.getUid() ){
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