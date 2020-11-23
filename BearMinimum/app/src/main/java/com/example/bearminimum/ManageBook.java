package com.example.bearminimum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

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


    }
}