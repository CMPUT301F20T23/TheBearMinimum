package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * AddBookActivity
 *
 * Activity allows user to create a new book.
 *
 * Nov. 6, 2020
 */
public class AddBookActivity extends AppCompatActivity {

    private EditText editTitleEditText;
    private EditText editAuthorEditText;
    private EditText editISBNEditText;
    private EditText editDescrEditText;
    private Button addbook_button;

    private Button isbnAddBookButton;
    private FirebaseFirestore db;

    private boolean valid = false;

    private final static int SEARCH_REQUEST = 876;
    private final static int SEARCH_OK = 1;
    private final static int SEARCH_INVALID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        //initialize variables
        editTitleEditText = findViewById(R.id.editTitle);
        editAuthorEditText = findViewById(R.id.editAuthor);
        editISBNEditText = findViewById(R.id.editISBN);
        editDescrEditText = findViewById(R.id.editDescr);
        addbook_button = findViewById(R.id.addbook_button);
        isbnAddBookButton = findViewById(R.id.isbn_add_book_button);

        editTitleEditText.setVisibility(View.INVISIBLE);
        editAuthorEditText.setVisibility(View.INVISIBLE);
        editDescrEditText.setVisibility(View.INVISIBLE);
        addbook_button.setVisibility(View.INVISIBLE);
        editISBNEditText.setVisibility(View.INVISIBLE);

        isbnAddBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), isbn_search_book.class);
                Log.i("ISBN","FAIL");
                startActivityForResult(intent, SEARCH_REQUEST);
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
                    if (bookISBN.length() != 10 && bookISBN.length() != 13) {
                        Toast.makeText(getBaseContext(), "ISBN must be 10 or 13 digits", Toast.LENGTH_SHORT).show();
                        return;
                    } else
                        valid = false;
                    data.put("title", bookTitle);
                    data.put("author", bookAuthor);
                    data.put("isbn", bookISBN);
                    data.put("description", bookDescr);
                    data.put("bookid", "");
                    data.put("borrower", "~");
                    data.put("status", "available");
                    data.put("owner_scan","False");
                    data.put("borrower_scan","False");
                    data.put("owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    data.put("requests", new ArrayList<String>());
                    data.put("latitude", "");
                    data.put("longitude", "");
                    db.collection("books")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("DEBUG", "Data has been added successfully!");
                                    documentReference.update("bookid", documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("DEBUG", "Data could not be added!" + e.toString());
                                }
                            });
                    finish();
                } else {
                    Toast.makeText(findViewById(R.id.add_book_activity).getContext(), "invalid fields provided", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if the scanned isbn couldn't be found in google books, offer manual input
        if (requestCode == SEARCH_REQUEST && resultCode == SEARCH_INVALID) {
            Log.d("addbook", "got invalid search result");
            editTitleEditText.setVisibility(View.VISIBLE);
            editAuthorEditText.setVisibility(View.VISIBLE);
            editDescrEditText.setVisibility(View.VISIBLE);
            editISBNEditText.setVisibility(View.VISIBLE);
            addbook_button.setVisibility(View.VISIBLE);
            Toast.makeText(findViewById(R.id.add_book_activity).getContext(), "invalid isbn, try manual entry", Toast.LENGTH_SHORT).show();
        } else if (requestCode == SEARCH_REQUEST && resultCode == SEARCH_OK) {
            finish();
        }
    }
}