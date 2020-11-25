package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ManageBook extends AppCompatActivity {

    private FirebaseFirestore db;
    public TextView owner;
    public TextView borrower;
    public EditText borrowerConfirm;
    public EditText denoteISBN;
    public Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_book);

        owner = findViewById(R.id.owner_TextView);
        borrower = findViewById(R.id.Borrower_TextView);
        borrowerConfirm = findViewById(R.id.editText_borrower_confirm);
        denoteISBN = findViewById(R.id.editText_owner_denote);
        confirm = findViewById(R.id.owner_button);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                db = FirebaseFirestore.getInstance();

                String ISBN = denoteISBN.getText().toString();
                try {
                    db.collection("books")
                            .whereEqualTo("isbn", ISBN)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            Log.d("USER", doc.getId() + " => " + doc.getData());
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String docID = doc.getId();
                                            String userID = user.getUid();
                                            String bookstatus = doc.getString("status");
                                            String bookowner = doc.getString("owner");

                                            if ((!bookstatus.equals("borrowed")) && (bookowner != userID)) {
                                                db.collection("book").document(docID).update("status", "borrowed");
                                                String current = doc.getString("status");
                                                Toast.makeText(ManageBook.this, "Success!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        Log.d("USER", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                } catch (NullPointerException e) {
                    System.out.print("NullPointerException Caught");
                    Log.d("USER", "exception");
                    Toast.makeText(ManageBook.this, "ISBN search not exist", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}