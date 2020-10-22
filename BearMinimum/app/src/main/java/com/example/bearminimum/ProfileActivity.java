package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText phonenumber;
    private ImageView profileImg;

    private Button apply;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://thebearminimum-adecf.appspot.com/user_profile_images/" + user.getUid() + ".png");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.edit_username);
        email = findViewById(R.id.edit_email);
        phonenumber = findViewById(R.id.edit_phone);
        apply = findViewById(R.id.apply_profile_changes);

        profileImg = findViewById(R.id.profileImage);

        Glide.with(this.getBaseContext())
                .load(storageRef)
                .apply(new RequestOptions().override(profileImg.getHeight()))
                .into(profileImg);

        username.setText(user.getDisplayName());
        email.setText(user.getEmail());
        phonenumber.setText(user.getPhoneNumber());

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    private void updateUserProfile() {
        String newUsername = username.getText().toString();
        String newEmail = email.getText().toString();

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();
        if (username.length() == 0 || newEmail.length() == 0) {
            Snackbar.make(findViewById(R.id.profile_view), R.string.invalid_profile_changes, Snackbar.LENGTH_LONG).show();
            return;
        }
        user.updateProfile(profileChangeRequest);
        user.updateEmail(newEmail);
    }
}