package com.example.bearminimum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.internal.InternalTokenProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthPage extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private Button signinBtn;
    private EditText emailbox;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthPage.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            finish();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authpage);

        //emailbox = findViewById(R.id.signin_email);

        signinBtn = findViewById(R.id.button_signin);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                //createSignInIntent();
                //handleSignin(emailbox.getText().toString());
            }
        });
    }

    private void handleSignin(String email) {

        auth.signInWithEmailAndPassword(email, "")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException invalidEmail) {
                                //create user

                                //auth.create
                            }
                            catch (FirebaseAuthInvalidCredentialsException wrongPass) {
                                //handle invalid password
                            }
                            catch (Exception e) {
                                Log.d("MyDebug", e.getMessage());
                            }
                        } else {
                            //login, continue to main
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    public void createSignInIntent() {
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(true, false)
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()))
                .build();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                handleSignInResponse(resultCode, data);
            } else
                Snackbar.make(findViewById(R.id.authview), "Sign In Failed", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && getIntent().getExtras() == null) {
            startActivity(MainActivity.createIntent(this, null));
            finish();
        }
    }

    private void handleSignInResponse(int resultCode, @Nullable Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (resultCode == RESULT_OK) {
            if (response.isNewUser())
                createFirestoreProfile();
            user = FirebaseAuth.getInstance().getCurrentUser();
            db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid())
                    .update("email", user.getEmail());
            startActivity(MainActivity.createIntent(this, response));
            finish();
        } else {
            if (response == null) {
                //back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_network);
                return;
            }
            showSnackbar(R.string.unknown_error);
        }
    }

    private void createFirestoreProfile() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user.getDisplayName() == null) {
            //Log.d("MyDebug", "display name null");
            for (UserInfo profile : user.getProviderData()) {
                Log.d("MyDebug", profile.getProviderId());
                if (profile.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)) {
                    Log.d("MyDebug", "goog");
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(profile.getDisplayName())
                            .build();
                    user.updateProfile(profileChangeRequest);
                    break;
                }
            }
        }

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", user.getDisplayName());
        //newUser.put("email", user.getEmail());
        newUser.put("uid", user.getUid());
        newUser.put("phonenumber", "");

        db.collection("users").document(user.getUid())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MyDebug", "New user succesfully added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MyDebug", "Error writing new user document", e);
                    }
                });
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(findViewById(R.id.authview), errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        //prevent going back
        /*finish();
        System.exit(0);

         */
    }
}