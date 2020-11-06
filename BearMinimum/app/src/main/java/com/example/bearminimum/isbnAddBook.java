package com.example.bearminimum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * isbnAddBook
 *
 * Connects to the isbn_search_book class by taking the information
 * obtained from Google Books Api, and displaying it in the fields
 *
 * Nov. 6, 2020
 */

public class isbnAddBook extends AppCompatActivity {
    private TextView textViewName;
    private TextView textViewAuthor;
    private TextView textViewDes;
    private TextView textViewISBN;



    @Override
    protected void onCreate(@Nullable Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.isbn_add_book);
        String name = getIntent().getExtras().getString("name");
        String des = getIntent().getExtras().getString("des");
        String author = getIntent().getExtras().getString("author");
        String isbn = getIntent().getExtras().getString("isbn");


        textViewName = findViewById(R.id.isbn_book_name);
        textViewAuthor = findViewById(R.id.isbn_author);
        textViewDes = findViewById(R.id.isbn_description);
        textViewISBN = findViewById(R.id.isbn_ISBN);

        textViewName.setText(name);
        textViewDes.setText(des);
        textViewISBN.setText(isbn);
        textViewAuthor.setText(author);
    }


    /**
     * Packages all the information of the book and sends
     * it to isbn_search_book class, so that it can be used
     *
     * @param view
     */

    public void isbn_add_button(View view){

        String name = textViewName.getText().toString();
        String author = textViewAuthor.getText().toString();
        String isbn = textViewISBN.getText().toString();
        String des = textViewDes.getText().toString();

        Log.i("Riky","Here1");
        Intent intent1 = new Intent(isbnAddBook.this,isbn_search_book.class);
        intent1.putExtra("name",name);
        intent1.putExtra("author",author);
        intent1.putExtra("isbn",isbn);
        intent1.putExtra("des",des);

        setResult(1,intent1);
        finish();
    }


    /**
     * Creates an intent for isbn_search_book class without
     * sending the book information
     *
     * @param view
     */

    public void isbn_cancel_button(View view){
        Intent intent1 = new Intent(isbnAddBook.this,isbn_search_book.class);
        setResult(0,intent1);
        finish();
    }




}
