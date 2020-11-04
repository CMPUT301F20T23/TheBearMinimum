package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class BookInfo extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 1;
    private ImageView imageView;
    private Button insertImg, removeImg;
    private TextView t1,t2,t3,t4;
    private String name, author, ISBN, descr, bookid;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        insertImg = findViewById(R.id.btnInsert);
        removeImg = findViewById(R.id.removeImage);
        imageView = findViewById(R.id.imgBook);
        t1 = findViewById(R.id.bookName);
        t2 = findViewById(R.id.bookAuthor);
        t3 = findViewById(R.id.bookISBN);
        t4 = findViewById(R.id.bookDescr);

        name=getIntent().getStringExtra("NAME");
        author=getIntent().getStringExtra("AUTHOR");
        ISBN=getIntent().getStringExtra("ISBN");
        descr=getIntent().getStringExtra("DESCRIPTION");
        bookid = getIntent().getStringExtra("BOOKID");

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

        removeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageReference.child("book_cover_images/" + bookid).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(BookInfo.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(BookInfo.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST_CODE  && resultCode==RESULT_OK && data!=null){
            Uri imgData = data.getData();
            imageView.setImageURI(imgData);

            StorageReference ref = storageReference.child("book_cover_images/" + bookid);

            ref.putFile(imgData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Snackbar.make(findViewById(android.R.id.content),"Successfully Uploaded", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


}