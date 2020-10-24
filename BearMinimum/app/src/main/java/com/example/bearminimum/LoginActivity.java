package com.example.bearminimum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.ProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private EditText emailField;

    private Button checkInfoBtn;
    private Button continueAuthBtn;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private CollectionReference usersRef;

    private ProgressIndicator waiting;

    private int stage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.email_box);
        passwordField = findViewById(R.id.password_box);
        passwordField.setVisibility(View.INVISIBLE);
        usernameField = findViewById(R.id.username_box);
        usernameField.setVisibility(View.INVISIBLE);
        waiting = findViewById(R.id.authwaiting);
        waiting.setVisibility(View.INVISIBLE);

        checkInfoBtn = findViewById(R.id.verify_button);
        checkInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });
        checkInfoBtn.setVisibility(View.INVISIBLE);

        continueAuthBtn = findViewById(R.id.signupin_button);
        continueAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try to login
                switch (stage) {
                    case 0:
                        determineNewuser();
                        break;
                    case 1:
                        attemptLogin();
                        break;
                    case 2:
                        createAccount();
                }
            }
        });

        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                continueAuthBtn.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    private void attemptLogin() {
        auth.signInWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException wrongEmail) {
                                Log.d("MyDebug", "wrong email");
                            }
                            catch (FirebaseAuthInvalidCredentialsException wrongPass) {
                                Log.d("MyDebug", "wrong pass");
                            }
                            catch (Exception e) {
                                Log.d("MyDebug", e.getMessage());
                            }
                        } else {
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void createAccount() {
        auth.createUserWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException invalidUser) {
                                Log.d("MyDebug", "user DNE");
                            }
                            catch (FirebaseAuthWeakPasswordException weakPasswordException) {
                                Log.d("MyDebug", "weak pass");
                            }
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Log.d("MyDebug", "malformed email");
                            }
                            catch (Exception e) {
                                Log.d("MyDebug", e.getMessage());
                            }
                        } else {
                            createProfile();
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void createProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(usernameField.getText().toString())
                .build();
        user.updateProfile(profileChangeRequest);

        Map<String, Object> newUser = new HashMap<>();
        newUser.put("username", usernameField.getText().toString());
        newUser.put("email", user.getEmail());
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

    private void determineNewuser() {
        auth.signInWithEmailAndPassword(emailField.getText().toString(), emailField.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException invalidUser) {
                                Log.d("MyDebug", "need new account");
                                passwordField.setVisibility(View.VISIBLE);
                                usernameField.setVisibility(View.VISIBLE);
                                checkInfoBtn.setVisibility(View.VISIBLE);
                                stage = 2;
                            }
                            catch (FirebaseAuthInvalidCredentialsException wrongPass) {
                                passwordField.setVisibility(View.VISIBLE);
                                continueAuthBtn.setEnabled(true);
                                stage = 1;
                            }
                            catch (Exception e) {
                                Log.d("MyDebug", e.getMessage());
                            }
                        }
                    }
                });
    }

    private void checkInfo() {
        waiting.setVisibility(View.VISIBLE);
        String newUsername = usernameField.getText().toString();
        Query query = usersRef.whereEqualTo("username", newUsername);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("MyDebug", "query callback");
                waiting.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        Log.d("MyDebug", "username is free");
                        //user does not exist
                        if (stage == 2 && emailField.getText().toString().length() > 0 && usernameField.getText().toString().length() > 0 && passwordField.getText().toString().length() > 0) {
                            continueAuthBtn.setEnabled(true);
                        } else if (stage == 1 && emailField.getText().toString().length() > 0 && passwordField.getText().toString().length() > 0) {
                            continueAuthBtn.setEnabled(true);
                        }
                    } else {
                        Snackbar.make(findViewById(R.id.loginview), "Username Taken", Snackbar.LENGTH_LONG).show();
                        continueAuthBtn.setEnabled(false);
                        Log.d("MyDebug", "username not free");
                    }
                } else
                    Log.d("MyDebug", task.getException().toString());
            }
        });
    }
}