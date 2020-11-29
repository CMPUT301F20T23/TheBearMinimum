package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class ScanBook extends AppCompatActivity {

    private EditText ownerdenote;
    private Button owner_button;
    private Button scan;
    private FirebaseFirestore db;

    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);

        ownerdenote =findViewById(R.id.editText_owner_denote);
        owner_button=findViewById(R.id.owner_button);
        scan = findViewById(R.id.scan_button);

        // Use camera to extract an ISBN from barcode
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to access to the permission first
                getCameraPermission();
            }
        });

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
                                            String bborrower=document.getString("borrower");
                                            String owner_scan=document.getString("owner_scan");
                                            String borrower_scan=document.getString("borrower_scan");

                                            // in order to access, the status must be either accepted or borrowed
                                            if ((!bookstatus.equals("accepted")) && (!bookstatus.equals("borrowed"))) {
                                                Toast.makeText(ScanBook.this, "Invalid Request.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            else {
                                                List<String> requesters = (List<String>) document.getData().get("requests");
                                                //owner first time scan the book
                                                if((bookstatus.equals("accepted")) && userID.equals(bookowner) && owner_scan.equals("False")){
                                                    db.collection("books").document(docID).update("owner_scan", "True");
                                                    owner_scan = "True";
                                                    Toast.makeText(ScanBook.this, "You are now denoting the book as borrowed ", Toast.LENGTH_SHORT).show();
                                                }
                                                //owner has already scanned the book
                                                else if((bookstatus.equals("accepted")) && userID.equals(bookowner) && owner_scan.equals("True")){
                                                    Toast.makeText(ScanBook.this, "Book is already denoted as borrowed ", Toast.LENGTH_SHORT).show();

                                                }
                                                //borrower first time scan the book
                                                else if((bookstatus.equals("accepted")) && userID.equals(requesters.get(0)) && borrower_scan.equals("False")){
                                                    db.collection("books").document(docID).update("borrower_scan", "True");
                                                    borrower_scan = "True";
                                                    Toast.makeText(ScanBook.this, "You are now confirming the book as borrowed ", Toast.LENGTH_SHORT).show();

                                                }
                                                //borrower has already scanned the book
                                                else if((bookstatus.equals("accepted")) && userID.equals(requesters.get(0)) && borrower_scan.equals("True")){
                                                    Toast.makeText(ScanBook.this, "Book is already confirmed as borrowed ", Toast.LENGTH_SHORT).show();

                                                }
                                                //owner first time scan the book
                                                else if((bookstatus.equals("borrowed")) && userID.equals(bookowner) && owner_scan.equals("False")){
                                                    db.collection("books").document(docID).update("owner_scan", "True");
                                                    owner_scan = "True";
                                                    Toast.makeText(ScanBook.this, "You are now denoting the book as returned ", Toast.LENGTH_SHORT).show();
                                                }
                                                //owner has already scanned the book
                                                else if((bookstatus.equals("borrowed")) && userID.equals(bookowner) && owner_scan.equals("True")){
                                                    Toast.makeText(ScanBook.this, "Book is already denoted as returned ", Toast.LENGTH_SHORT).show();

                                                }
                                                //borrower first time scan the book
                                                else if((bookstatus.equals("borrowed")) && userID.equals(bborrower) && borrower_scan.equals("False")){
                                                    db.collection("books").document(docID).update("borrower_scan", "True");
                                                    borrower_scan = "True";
                                                    Toast.makeText(ScanBook.this, "You are now confirming the book as returned ", Toast.LENGTH_SHORT).show();

                                                }
                                                //borrower has already scanned the book
                                                else if((bookstatus.equals("borrowed")) && userID.equals(bborrower) && borrower_scan.equals("True")){
                                                    Toast.makeText(ScanBook.this, "Book is already confirmed as returned ", Toast.LENGTH_SHORT).show();

                                                }
                                                if (bookstatus.equals("accepted") && owner_scan.equals("True") && borrower_scan.equals("True")){
                                                    db.collection("books").document(docID).update("status", "borrowed");
                                                    db.collection("books").document(docID).update("borrower", requesters.get(0));
                                                    db.collection("books").document(docID).update("latitude", "");
                                                    db.collection("books").document(docID).update("longitude", "");
                                                    db.collection("books").document(docID).update("owner_scan", "False");
                                                    db.collection("books").document(docID).update("borrower_scan", "False");


                                                }
                                                if (bookstatus.equals("borrowed") && owner_scan.equals("True") && borrower_scan.equals("True")){
                                                    db.collection("books").document(docID).update("status", "available");
                                                    db.collection("books").document(docID).update("borrower", "~");
                                                    db.collection("books").document(docID).update("owner_scan", "False");
                                                    db.collection("books").document(docID).update("borrower_scan", "False");
                                                    db.collection("books").document(docID).update("requests", new ArrayList<String>());

                                            }

                                            //update book status when both owner and borrower scan the book



                                            }
                                        }
                                    }

                                    else {
                                        // Report if the document is not received
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

    // Open camera to scan a barcode
    public void scanBarcode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(FetchCode.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("scan code");
        integrator.initiateScan();
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     * Handle the result of the scan activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Verify the scanned result
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scan Result");
                builder.setPositiveButton("Another Scan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //click this button to scan the barcode again, or scan another barcode
                        scanBarcode();
                    }
                }).setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //click this button to put the extracted ISBN into the textbox
                        ownerdenote.setText(result.getContents());
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // If the camera permission is received, then scanning barcode is allowed
    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            scanBarcode();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_ACCESS_CAMERA);
        }
    }

    /**
     * Handles the result of the request for camera permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanBarcode();
                }
            }
        }

    }


}
