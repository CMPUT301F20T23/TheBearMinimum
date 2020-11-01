package com.example.bearminimum;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class BookInfo extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 123;
    private ImageView imageView;
    private Button insertImg;
    private TextView t1,t2,t3,t4;
    private String name, author, ISBN, descr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        insertImg = findViewById(R.id.btnInsert);
        imageView = findViewById(R.id.imgBook);
        t1 = findViewById(R.id.bookName);
        t2 = findViewById(R.id.bookAuthor);
        t3 = findViewById(R.id.bookISBN);
        t4 = findViewById(R.id.bookDescr);

        name=getIntent().getStringExtra("NAME");
        author=getIntent().getStringExtra("AUTHOR");
        ISBN=getIntent().getStringExtra("ISBN");
        descr=getIntent().getStringExtra("DESCRIPTION");

        t1.setText(name);
        t2.setText(author);
        t3.setText(ISBN);
        t4.setText(descr);

        insertImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an image"), GALLERY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE  && resultCode==RESULT_OK && data!=null){
            Uri imgData = data.getData();
            imageView.setImageURI(imgData);
        }
    }
}