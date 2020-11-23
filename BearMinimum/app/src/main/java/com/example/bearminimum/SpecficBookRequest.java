package com.example.bearminimum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class SpecficBookRequest extends AppCompatActivity {

    public RecyclerView specBook;
    public ArrayList<Book> selectedBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specfic_book_request);
    }
}