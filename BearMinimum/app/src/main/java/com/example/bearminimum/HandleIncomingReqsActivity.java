package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HandleIncomingReqsActivity
 *
 * Used to show requests from other users for a certain book
 * owned by the current user
 *
 * Nov. 6, 2020
 */

public class HandleIncomingReqsActivity extends AppCompatActivity {

    private ListView requestView;
    private IncomingRequestsAdapter adapter;
    private Book book;
    private String bid;
    private ArrayList<User> users;
    private ArrayList<String> uids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_incoming_reqs);

        //get listview for requests
        requestView = findViewById(R.id.request_view);

        //an array of firebase user ids that are requesting the book in question
        uids = new ArrayList<>();
        //an array of usernames associated with the uids requesting the book
        users = new ArrayList<>();

        //the book id for the selected book
        bid = getIntent().getExtras().getString("bookid");

        //get the specified book from firestore
        FirebaseFirestore.getInstance().collection("books").document(bid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map data = task.getResult().getData();
                    String title = (String) data.get("title");
                    String author = (String) data.get("author");
                    String bid = (String) data.get("bookid");
                    String desc = (String) data.get("description");
                    String isbn = (String) data.get("isbn");
                    String status = (String) data.get("status");
                    String borrower = (String) data.get("borrower");
                    String owner_scan=(String) data.get("owner_scan") ;
                    String borrower_scan=(String) data.get("borrower_scan") ;
                    String lat = (String) data.get("latitude");
                    String longitude = (String) data.get("longitude");
                    uids = (ArrayList<String>) data.get("requests");
                    book = new Book(title,author, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),borrower,desc,isbn,status,bid, lat, longitude, owner_scan, borrower_scan);
                    //custom adapter for displaying lists
                    adapter = new IncomingRequestsAdapter(users, getBaseContext(), book.getBid());
                    requestView.setAdapter(adapter);

                    //fill users array from uid gotten from firestore
                    getUsers();
                }
            }
        });

        FirebaseFirestore.getInstance().collection("books").document(bid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    if (value.exists()) {
                        Map data = value.getData();
                        if (!data.get("status").equals("requested")) {
                            finish();
                        }
                    }
                }
            }
        });
    }

    /**
     * This takes the list of uids and gets the associated usernames from the firestore users collection
     */
    private void getUsers() {
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
        for (String uid : uids) {
            //for each uid, get the user from firestore
            usersRef.document(uid).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                Map data = task.getResult().getData();
                                String email = (String) data.get("email");
                                String phone = (String) data.get("phonenumber");
                                String uid = (String) data.get("uid");
                                String username = (String) data.get("username");
                                users.add(new User(email, phone, uid, username));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    public String getBookid() {
        return book.getBid();
    }
}