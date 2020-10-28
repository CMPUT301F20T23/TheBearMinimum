package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity implements Reauth.OnFragmentInteractionListener{

    private EditText username;
    private EditText email;
    private EditText phonenumber;
    private ImageView profileImg;
    private Button apply;
    private Button signout;
    private Button checkName;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private DocumentReference userRef;
    StorageReference storageRef;
    AuthCredential credential;

    private String newUsername;

    private static final int PICK_IMAGE = 1188;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(AuthPage.createIntent(this));
            finish();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user.reload();
        Log.d("MyDebug", "user reloaded");

        //init firebase
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        userRef = usersRef.document(user.getUid());
        storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/user_profile_images/" + user.getUid());

        //get layout elements
        username = findViewById(R.id.edit_username);
        email = findViewById(R.id.edit_email);
        phonenumber = findViewById(R.id.edit_phone);
        apply = findViewById(R.id.apply_profile_changes);
        signout = findViewById(R.id.signout);
        checkName = findViewById(R.id.checkName);
        profileImg = findViewById(R.id.profileImage);

        //init elements
        apply.setEnabled(false);
        email.setText(user.getEmail());
        //load profile image
        Glide.with(this.getBaseContext())
                .load(storageRef)
                .placeholder(R.drawable.logo_books)
                .apply(new RequestOptions().override(profileImg.getHeight()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profileImg);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfileImage();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(v.getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getBaseContext(), AuthPage.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
        checkName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNameAvailable();
            }
        });

        //text listeners
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                apply.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                apply.setEnabled(false);
            }
        });

        //get user info from firestore
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null && snapshot.exists()) {
                    username.setText((String) snapshot.getData().get("username"));
                    phonenumber.setText((String) snapshot.getData().get("phonenumber"));
                    Log.d("MyDebug", "document event");
                } else {
                    Log.d("MyDebug", "doc snapshot null");
                }
            }
        });
    }

    /**
     * attempts to apply requested changes
     */
    private void updateUserProfile() {
        newUsername = username.getText().toString();
        String newEmail = email.getText().toString();
        String newPhonenumber = phonenumber.getText().toString();

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();
        if (username.length() == 0 || newEmail.length() == 0) {
            Snackbar.make(findViewById(R.id.profile_view), R.string.invalid_profile_changes, Snackbar.LENGTH_LONG).show();
            return;
        }

        user.updateProfile(profileChangeRequest);

        if (!user.getEmail().equals(email.getText().toString())) {
            new Reauth().show(getSupportFragmentManager(), "REAUTH");
        }

        //update firestore record
        userRef.update("username", newUsername, "phonenumber", newPhonenumber);
    }

    private void checkNameAvailable() {
        newUsername = username.getText().toString();
        Query query = usersRef.whereNotEqualTo("uid", user.getUid()).whereEqualTo("username", newUsername);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("MyDebug", "query callback");
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        Log.d("MyDebug", "username is free");
                        //user does not exist
                        if (email.getText().toString().length() > 0) {
                            apply.setEnabled(true);
                        }
                    } else {
                        Snackbar.make(findViewById(R.id.profile_view), "Username Taken", Snackbar.LENGTH_LONG).show();
                        apply.setEnabled(false);
                        Log.d("MyDebug", "username not free");
                    }
                } else
                    Log.d("MyDebug", task.getException().toString());
            }
        });
    }

    /**
     * use system dialog to select image
     */
    private void changeProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //pass to callback to handle image upload
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //upload chosen image to firestore as profile image
        if (requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            UploadTask uploadTask = storageRef.putFile(imageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("MyDebug", "failed upload", e);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("MyDebug", "uploaded as " + taskSnapshot.getMetadata().getPath());
                    //force glide to update image
                    Glide.with(getBaseContext())
                            .load(storageRef)
                            .placeholder(R.drawable.logo_books)
                            .apply(new RequestOptions().override(profileImg.getHeight()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(profileImg);
                }
            });
        }
    }

    @Override
    public void onOkPressed(String pass) {
        //get user credential
        credential = EmailAuthProvider.getCredential(user.getEmail(), pass);
        user.reauthenticate(credential);

        //continue with email update after reauth
        user.verifyBeforeUpdateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    }
                    catch (Exception e) {
                        Log.d("MyDebug", e.getMessage());
                    }
                } else {
                    Log.d("MyDebug", "sent verify to " + email.getText().toString());
                    Snackbar.make(findViewById(R.id.profile_view), "Sent email, changes will be visible on next login after verification", Snackbar.LENGTH_INDEFINITE)
                            .setAction("DISMISS", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            })
                            .show();
                }
            }
        });
    }
}