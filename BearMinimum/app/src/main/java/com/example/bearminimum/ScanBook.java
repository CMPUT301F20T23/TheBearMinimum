package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ScanBook extends AppCompatActivity {

    private EditText ownerdenote;
    private Button owner_button;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);

        ownerdenote =findViewById(R.id.editText_owner_denote);
        owner_button=findViewById(R.id.owner_button);


        // Owner scan ISBN to denote book as borrowed
        owner_button.setOnClickListener(new View.OnClickListener(){
            @Override
                public void onClick(View V) {
                //Firestore
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                db = FirebaseFirestore.getInstance();


                String bookISBN = ownerdenote.getText().toString();

                try {
                    db.collection("books")
                            .whereEqualTo("isbn", bookISBN)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            Log.d("QIXIN", document.getId() + " => " + document.getData());
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String docID = document.getId();
                                            String userID = user.getUid();
                                            String bookstatus = document.getString("status");
                                            String bookowner = document.getString("owner");
                                            String bookborrower=document.getString("borrower;");
                                            String owner_scan=document.getString("owner_scan");
                                            String borrower_scan=document.getString("borrower_scan");

                                            if (!bookstatus.equals("accepted") ) {
                                                Toast.makeText(ScanBook.this, "Request for this book is not accepted", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                //owner first time scan the book
                                                if(userID==bookowner && owner_scan =="False"){
                                                    db.collection("books").document(docID).update("owner_scan", "True");
                                                    Toast.makeText(ScanBook.this, "You are now denoting the book as borrowed ", Toast.LENGTH_SHORT).show();
                                                }
                                                //owner has already scanned the book
                                                else if(userID==bookowner && owner_scan =="True"){
                                                    Toast.makeText(ScanBook.this, "Book is already denoted as borrowed ", Toast.LENGTH_SHORT).show();

                                                }
                                                //borrower first time scan the book
                                                else if(userID==bookborrower && borrower_scan =="False"){
                                                    db.collection("books").document(docID).update("borrower_scan", "True");
                                                    Toast.makeText(ScanBook.this, "You are now confirming the book as borrowed ", Toast.LENGTH_SHORT).show();

                                                }
                                                //borrower has already scanned the book
                                                else if(userID==bookborrower && borrower_scan =="True"){
                                                    Toast.makeText(ScanBook.this, "Book is already confirmed as borrowed ", Toast.LENGTH_SHORT).show();

                                                }

                                            }

                                            //update book status when both owner and borrower scan the book
                                            if (bookstatus=="accepted" && owner_scan=="True" && borrower_scan=="True"){
                                                db.collection("books").document(docID).update("status", "borrowed");
                                            }
                                        }
                                    }

                                    else {

                                        Log.d("QIXIN", "Error getting documents: ", task.getException());
                                    }
                                }


                            });
                //catch exception when isbn for the book doesn't exist
                } catch (NullPointerException e){

                    Log.d("QIXIN","exception");
                    Toast.makeText(ScanBook.this,"ISBN search not exist",Toast.LENGTH_SHORT).show();

                }


            }

        });






    }



}