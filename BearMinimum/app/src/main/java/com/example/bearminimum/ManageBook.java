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

public class ManageBook extends AppCompatActivity {

    private EditText ownerdenote;
    private  EditText borrowerscan;
    private Button owner_button;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_book);

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

                                            if ((!bookstatus.equals("borrowed")) && (bookowner != userID)) {
                                                db.collection("book").document(docID).update("status", "borrowed");

                                                String now = document.getString("status");
                                                Toast.makeText(ManageBook.this, "Success!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    else {

                                        Log.d("QIXIN", "Error getting documents: ", task.getException());
                                    }
                                }


                            });

                } catch (NullPointerException e){
                    System.out.print("NullPointerException Caught");
                    Log.d("QIXIN","exception");
                    Toast.makeText(ManageBook.this,"ISBN search not exist",Toast.LENGTH_SHORT).show();

                }


            }

        });






    }



}