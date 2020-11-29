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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.WriteResult;

import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * ViewBookActivity
 *
 * Activity that allows user to view book details, and/or edit
 * those details depending on where this activity is being
 * called from
 *
 * Nov. 6, 2020
 */
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
        intent.putExtra("STATUS", book.getStatus());
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

    /**
     * This is the view that only allows users to view book info
     * and not edit them.
     * Also allows user to make or withdraw a request on a book
     *
     * called from the SearchActivity
     */

    private void showViewable() {
        setContentView(R.layout.activity_view_book_info);

        imageView = findViewById(R.id.view_book_image);
        t1 = findViewById(R.id.view_book_title);
        t2 = findViewById(R.id.view_book_author);
        t3 = findViewById(R.id.view_book_isbn);
        t4 = findViewById(R.id.view_book_desc);
        TextView t5 = findViewById(R.id.view_book_owner);

        //for requesting books
        ToggleButton requestButton = findViewById(R.id.request_button);

        name=getIntent().getStringExtra("NAME");
        author=getIntent().getStringExtra("AUTHOR");
        ISBN=getIntent().getStringExtra("ISBN");
        descr=getIntent().getStringExtra("DESCRIPTION");
        bookid = getIntent().getStringExtra("BOOKID");
        String status = getIntent().getStringExtra("STATUS");
        if (status.equals("borrowed") || status.equals("accepted"))
            requestButton.setVisibility(View.GONE);

        StorageReference storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/book_cover_images/" + bookid);
        //load profile image
        Glide.with(this.getBaseContext())
                .load(storageRef)
                .placeholder(R.drawable.book_logo_white)
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

        //get current user
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //set toggle button accordingly
        //if user is in requested list, set it to "withdraw request" state
        db.collection("books").document(bookid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            //get all requests
                            List<String> requests = (List<String>) task.getResult().get("requests");

                            //check if user is in list
                            if (requests.contains(currentUser)) {
                                requestButton.setChecked(true);
                            } else {
                                Log.d(TAG, "not yet requested");
                            }
                        } else {
                            Log.d(TAG, "couldn't get the doc");
                        }
                    }
                });


        //pressing the request button
        requestButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DocumentReference requestedBook = db.collection("books").document(bookid);
                if (b) {
                    //button pressed, book is requested
                    //add uid of current user into the requests array
                    requestedBook.update("requests", FieldValue.arrayUnion(currentUser));

                    //update status accordingly
                    String status = getIntent().getStringExtra("STATUS");
                    if (status.equals("available")) {
                        requestedBook.update("status", "requested");
                    }
                } else {
                    //button pressed, book request withdrawn
                    //remove uid of current user in the requests array
                    requestedBook.update("requests", FieldValue.arrayRemove(currentUser));

                    //update status accordingly
                    requestedBook.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                List<String> requests = (List<String>) task.getResult().get("requests");
                                if (requests.size() <= 0) {
                                    requestedBook.update("status", "available");
                                }
                            }
                        }
                    });

                }
            }
        });

    }

    /**
     * This is the view that allows users to edit book information.
     *
     * called from MainActivity for books that are owned by the user
     */
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

        String status = getIntent().getStringExtra("STATUS");
        if (status.equals("borrowed") || status.equals("accepted") || status.equals("requested"))
            apply.setVisibility(View.GONE);

        StorageReference storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/book_cover_images/" + bookid);
        //load profile image
        Glide.with(this.getBaseContext())
                .load(storageRef)
                .placeholder(R.drawable.book_logo_white)
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

    /**
     * Allows user to delete a book from the database
     * Only on books owned by the user
     *
     * @param bookID    id of the book being deleted
     */
    public void deleteBook(String bookID) {
            storageReference.child("book_cover_images/" + bookid).delete();
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