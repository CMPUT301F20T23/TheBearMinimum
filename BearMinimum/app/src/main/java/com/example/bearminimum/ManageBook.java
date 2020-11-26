package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ManageBook extends AppCompatActivity {

    private FirebaseFirestore db;
    public TextView owner;
    public TextView borrower;
    public EditText borrowerConfirm;
    public EditText denoteISBN;
    public Button confirm;
    public Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_book);

        owner = findViewById(R.id.owner_TextView);
        borrower = findViewById(R.id.Borrower_TextView);
        borrowerConfirm = findViewById(R.id.editText_borrower_confirm);
        denoteISBN = findViewById(R.id.editText_owner_denote);
        confirm = findViewById(R.id.owner_button);
        scan = findViewById(R.id.scanCode);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanBarcode();
            }
        });

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
                                            String bookborrower=doc.getString("borrower;");
                                            String bookISBN = doc.getString("isbn");
                                            String owner_scan=doc.getString("owner_scan");
                                            String borrower_scan=doc.getString("borrower_scan");

                                            if (!bookstatus.equals("accepted")){
                                                Toast.makeText(ManageBook.this,"Request for this book is not accepted", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                //owner first time scan the book
                                                if(userID.equals(bookowner) && owner_scan.equals("False")){
                                                    db.collection("books").document(docID).update("owner_scan", "True");
                                                    Toast.makeText(ManageBook.this, "You are now denoting the book as borrowed ", Toast.LENGTH_SHORT).show();
                                                }
                                                //owner has already scanned the book
                                                else if(userID.equals(bookowner) && owner_scan.equals("True")){
                                                    Toast.makeText(ManageBook.this, "Book is already denoted as borrowed ", Toast.LENGTH_SHORT).show();

                                                }
                                                //borrower first time scan the book
                                                else if(userID.equals(bookborrower) && borrower_scan.equals("False")){
                                                    db.collection("books").document(docID).update("borrower_scan", "True");
                                                    Toast.makeText(ManageBook.this, "You are now confirming the book as borrowed ", Toast.LENGTH_SHORT).show();

                                                }
                                                //borrower has already scanned the book
                                                else if(userID.equals(bookborrower) && borrower_scan.equals("True")) {
                                                    Toast.makeText(ManageBook.this, "Book is already confirmed as borrowed ", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            if (owner_scan.equals("True") && borrower_scan.equals("True") && bookstatus.equals("accepted")){
                                                db.collection("books").document(docID).update("status", "borrowed");
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

    public void ScanBarcode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(FetchCode.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("scan code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("scan result");
                builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScanBarcode();
                    }
                }).setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                Toast.makeText(this,"No Result", Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}