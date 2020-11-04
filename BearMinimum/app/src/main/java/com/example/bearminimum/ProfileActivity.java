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
import android.widget.TextView;

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

import org.w3c.dom.Text;

/**
 * This class is the activity for viewing and editing the current users information
 */
public class ProfileActivity extends AppCompatActivity implements Reauth.OnFragmentInteractionListener{

    private EditText username;
    private EditText email;
    private EditText phonenumber;
    private ImageView profileImg;
    private Button apply;
    private Button signout;
    private Button checkName;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private DocumentReference userRef;
    StorageReference storageRef;
    AuthCredential credential;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private String newUsername;
    private String userid;
    private Boolean isCurrentUser;

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

        userid = (String) getIntent().getExtras().get("UID");
        isCurrentUser = (Boolean) getIntent().getExtras().get("currentUser");

        if (isCurrentUser)
            showOwnedProfile();
        else
            showOtherProfile(userid);


    }

    /**
     * sets up the activity to display non editable information about some user
     * @param userid The id of the user to be displayed
     */
    private void showOtherProfile(String userid) {
        setContentView(R.layout.activity_other_profile);

        //get layout elements
        TextView username = findViewById(R.id.view_username);
        TextView email = findViewById(R.id.view_email);
        TextView phonenumber = findViewById(R.id.view_phone);
        profileImg = findViewById(R.id.profileImage);

        //init firebase
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        userRef = usersRef.document(userid);
        storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/user_profile_images/" + userid);

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

        //get user info from firestore
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null && snapshot.exists()) {
                    username.setText((String) snapshot.getData().get("username"));
                    email.setText((String) snapshot.getData().get("email"));
                    phonenumber.setText((String) snapshot.getData().get("phonenumber"));
                    Log.d("MyDebug", "document event");
                } else {
                    Log.d("MyDebug", "doc snapshot null");
                }
            }
        });
    }

    /**
     * sets up the activity to display editable info about the current user
     */
    private void showOwnedProfile() {
        setContentView(R.layout.activity_owned_profile);

        //get layout elements
        username = findViewById(R.id.edit_username);
        email = findViewById(R.id.edit_email);
        phonenumber = findViewById(R.id.edit_phone);
        apply = findViewById(R.id.apply_profile_changes);
        signout = findViewById(R.id.signout);
        checkName = findViewById(R.id.checkName);
        profileImg = findViewById(R.id.profileImage);

        //init firebase
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        userRef = usersRef.document(user.getUid());
        storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/user_profile_images/" + user.getUid());

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
                signOut(v);
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
            Snackbar sb = Snackbar.make(findViewById(R.id.profile_view), R.string.invalid_profile_changes, Snackbar.LENGTH_LONG);
            sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
            sb.show();
            return;
        }

        user.updateProfile(profileChangeRequest);

        if (!user.getEmail().equals(email.getText().toString())) {
            new Reauth().show(getSupportFragmentManager(), "REAUTH");
        }

        //update firestore record
        userRef.update("username", newUsername, "phonenumber", newPhonenumber);
    }

    /**
     * query firestore if the given username is taken by any other user
     */
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
                        Snackbar sb = Snackbar.make(findViewById(R.id.profile_view), "username Taken", Snackbar.LENGTH_LONG);
                        sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                        sb.show();
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
            //check that user actually selected an image
            if (data == null) {
                Log.d("MyDebug", "no image selected");
                return;
            }
            //get uri and start upload
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
                    Snackbar sb = Snackbar.make(findViewById(R.id.profile_view), "email sent, changes will be visible on next login after verification", Snackbar.LENGTH_INDEFINITE)
                            .setAction("DISMISS", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                    sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                    sb.show();
                }
            }
        });
    }

    /**
     * sign the user out
     * @param v is a view which authUI needs
     */
    public void signOut(View v) {
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
}