package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class AddBookActivity extends AppCompatActivity {

    private EditText editTitleEditText;
    private EditText editAuthorEditText;
    private EditText editISBNEditText;
    private EditText editDescrEditText;
    private Button addbook_button;

    private Button isbnAddBookButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        editTitleEditText = findViewById(R.id.editTitle);
        editAuthorEditText = findViewById(R.id.editAuthor);
        editISBNEditText = findViewById(R.id.editISBN);
        editDescrEditText = findViewById(R.id.editDescr);
        addbook_button = findViewById(R.id.addbook_button);
        isbnAddBookButton = findViewById(R.id.isbn_add_book_button);


        isbnAddBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBookActivity.this, isbn_search_book.class);
                Log.i("ISBN","FAIL");
                startActivity(intent);
                finish();
            }
        });

        addbook_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                HashMap<String, Object> data = new HashMap<>();
                final String bookTitle = editTitleEditText.getText().toString();
                final String bookAuthor = editAuthorEditText.getText().toString();
                final String bookISBN = editISBNEditText.getText().toString();
                final String bookDescr = editDescrEditText.getText().toString();

                if (bookTitle.length() > 0 && bookAuthor.length() > 0 && bookISBN.length() > 0) {
                    data.put("title", bookTitle);
                    data.put("author", bookAuthor);
                    data.put("isbn", bookISBN);
                    data.put("description", bookDescr);
                    db.collection("books").document(user.getUid())
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("DEBUG", "Data has been added successfully!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("DEBUG", "Data could not be added!" + e.toString());
                                }
                            });
                    finish();


                }
                ;
            }

        });




    }
}