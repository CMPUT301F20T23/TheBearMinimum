package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

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

    private int usernameQueryResult = 1;
    private String newUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        userRef = usersRef.document(user.getUid());

        Log.d("MyDebug", usersRef.getId());
        Log.d("MyDebug", userRef.getId());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists())
                    Log.d("MyDebug", "gettem");
                else
                    Log.d("MyDebug", "no such doc");
            }
        });

        username = findViewById(R.id.edit_username);
        email = findViewById(R.id.edit_email);
        phonenumber = findViewById(R.id.edit_phone);
        apply = findViewById(R.id.apply_profile_changes);
        signout = findViewById(R.id.signout);
        checkName = findViewById(R.id.checkName);

        email.setText(user.getEmail());

        profileImg = findViewById(R.id.profileImage);
        StorageReference storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/user_profile_images/" + user.getUid() + ".png");
        Glide.with(this.getBaseContext())
                .load(storageRef)
                .placeholder(R.drawable.logo_books)
                .apply(new RequestOptions().override(profileImg.getHeight()))
                .into(profileImg);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
        apply.setEnabled(false);

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

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (usernameQueryResult == 1 && username.getText().toString().length() > 0 && email.getText().toString().length() > 0) {
                    apply.setEnabled(true);
                } else
                    apply.setEnabled(false);
            }
        });

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
                if (usernameQueryResult == 1 && username.getText().toString().length() > 0 && email.getText().toString().length() > 0) {
                    apply.setEnabled(true);
                } else
                    apply.setEnabled(false);
            }
        });

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null && snapshot.exists()) {
                    username.setText((String) snapshot.getData().get("username"));
                    //email.setText((String) snapshot.getData().get("email"));
                    phonenumber.setText((String) snapshot.getData().get("phonenumber"));
                    Log.d("MyDebug", "document event");
                } else {
                    Log.d("MyDebug", "doc snapshot null");
                    Log.d("MyDebug", user.getUid());
                }
            }
        });
    }

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
        user.verifyBeforeUpdateEmail(newEmail);

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
                        usernameQueryResult = 1;
                        if (email.getText().toString().length() > 0) {
                            apply.setEnabled(true);
                        }
                    } else {
                        usernameQueryResult = 0;
                        Snackbar.make(findViewById(R.id.profile_view), "Username Taken", Snackbar.LENGTH_LONG).show();
                        apply.setEnabled(false);
                        Log.d("MyDebug", "username not free");
                    }
                } else
                    Log.d("MyDebug", task.getException().toString());
            }
        });
    }
}