package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

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

        requestView = findViewById(R.id.request_view);

        uids = new ArrayList<>();
        users = new ArrayList<>();

        adapter = new IncomingRequestsAdapter(users, this);
        requestView.setAdapter(adapter);

        bid = getIntent().getExtras().getString("bookid");

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
                    uids = (ArrayList<String>) data.get("requests");
                    book = new Book(title,author, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),borrower,desc,isbn,status,bid);

                    getUsers();
                }
            }
        });
    }

    private void getUsers() {
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
        for (String uid : uids) {
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
}