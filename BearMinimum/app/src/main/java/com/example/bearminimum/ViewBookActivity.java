package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.content.ContentValues.TAG;

public class ViewBookActivity extends AppCompatActivity {
    private static final int GALLERY_REQUEST_CODE = 1;
    private ImageView imageView;
    private TextView t1,t2,t3,t4;
    private String name, author, ISBN, descr, bookid;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private boolean isOwnedByUser;

    public static Intent createIntent(Book book, Context context, boolean isOwned) {
        Intent intent = new Intent(context, ViewBookActivity.class);
        intent.putExtra("NAME", book.getTitle());
        intent.putExtra("AUTHOR", book.getAuthor());
        intent.putExtra("ISBN", book.getISBN());
        intent.putExtra("DESCRIPTION", book.getDescription());
        intent.putExtra("BOOKID", book.getBid());
        intent.putExtra("ISOWNED", isOwned);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        isOwnedByUser = (boolean) getIntent().getExtras().get("ISOWNED");
        if (isOwnedByUser)
            showEditable();
        else
            showViewable();


    }

    private void showViewable() {
        setContentView(R.layout.activity_view_book_info);

        imageView = findViewById(R.id.view_book_image);
        t1 = findViewById(R.id.view_book_title);
        t2 = findViewById(R.id.view_book_author);
        t3 = findViewById(R.id.view_book_isbn);
        t4 = findViewById(R.id.view_book_desc);
        TextView t5 = findViewById(R.id.view_book_owner);

        name=getIntent().getStringExtra("NAME");
        author=getIntent().getStringExtra("AUTHOR");
        ISBN=getIntent().getStringExtra("ISBN");
        descr=getIntent().getStringExtra("DESCRIPTION");
        bookid = getIntent().getStringExtra("BOOKID");

        StorageReference storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/book_cover_images/" + bookid);
        //load profile image
        Glide.with(this.getBaseContext())
                .load(storageRef)
                .placeholder(R.drawable.logo_books)
                .apply(new RequestOptions().override(imageView.getHeight()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);

        t1.setText(name);
        t2.setText("by " + author);
        t3.setText(ISBN);
        t4.setMovementMethod(new ScrollingMovementMethod());
        t4.setText(descr);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books").document(bookid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String userid = (String) task.getResult().getData().get("owner");
                    db.collection("users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                t5.setText("owner: " + (String) task.getResult().getData().get("username"));
                            }
                        }
                    });
                }
            }
        });
    }

    private void showEditable() {
        setContentView(R.layout.activity_edit_book_info);

        Button insertImg = findViewById(R.id.btnInsert);
        Button removeImg = findViewById(R.id.removeImage);
        Button deleteBook = findViewById(R.id.delete_book_btn);
        Button apply = findViewById(R.id.edit_book_apply_btn);
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

        StorageReference storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/book_cover_images/" + bookid);
        //load profile image
        Glide.with(this.getBaseContext())
                .load(storageRef)
                .placeholder(R.drawable.logo_books)
                .apply(new RequestOptions().override(imageView.getHeight()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);

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
                                Toast.makeText(ViewBookActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ViewBookActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook(bookid);
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges(v);
            }
        });
    }

    private void applyChanges(View v) {
        if (t1.getText().length() > 0 && t2.getText().length() > 0 && t3.getText().length() > 0 && t4.getText().length() > 0) {
            FirebaseFirestore.getInstance().collection("books").document(getIntent().getExtras().getString("BOOKID"))
                    .update("title", t1.getText().toString(), "author", t2.getText().toString(), "isbn", t3.getText().toString(), "description", t4.getText().toString());
            Snackbar sb = Snackbar.make(v.getRootView(), "applied",Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
            sb.show();
        } else {
            Snackbar sb = Snackbar.make(v.getRootView(), "missing fields",Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
            sb.show();
        }
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

    //delete book
    public void deleteBook(String bookID) {
            FirebaseFirestore.getInstance().collection("books")
                    .document(bookID)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "book successfully deleted!");
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting book", e);
                        }
                    });
    }
}