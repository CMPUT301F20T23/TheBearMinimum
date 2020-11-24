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
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * LoginActivity
 *
 * This class handles the input and authentication of login information
 *
 * Nov.6, 2020
 */
public class LoginActivity extends AppCompatActivity {

    //ui elements
    private EditText usernameField;
    private EditText passwordField;
    private EditText emailField;
    private Button checkInfoBtn;
    private Button continueAuthBtn;
    private ProgressIndicator waiting;

    //Firebase references
    private FirebaseAuth auth;
    private CollectionReference usersRef;

    //determines what continue button does
    private int stage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //get ui elements
        emailField = findViewById(R.id.email_box);
        passwordField = findViewById(R.id.password_box);
        usernameField = findViewById(R.id.username_box);
        waiting = findViewById(R.id.authwaiting);
        checkInfoBtn = findViewById(R.id.verify_button);

        //init ui elements
        waiting.setVisibility(View.INVISIBLE);
        usernameField.setVisibility(View.INVISIBLE);
        passwordField.setVisibility(View.INVISIBLE);
        continueAuthBtn = findViewById(R.id.signupin_button);

        //various listeners
        continueAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting.setVisibility(View.VISIBLE);
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
        checkInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });
        checkInfoBtn.setVisibility(View.INVISIBLE);
        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                continueAuthBtn.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //init firebase ref
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    /**
     * tries to login with current email and password fields
     */
    private void attemptLogin() {
        auth.signInWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        waiting.setVisibility(View.INVISIBLE);
                        if (!task.isSuccessful()) {
                            //handle exception
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException wrongEmail) {
                                Log.d("MyDebug", "wrong email");
                                Snackbar sb = Snackbar.make(findViewById(R.id.loginview), "wrong username or password", Snackbar.LENGTH_SHORT);
                                sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                                sb.show();
                            } catch (FirebaseAuthInvalidCredentialsException wrongPass) {
                                Log.d("MyDebug", "wrong pass");
                                Snackbar sb = Snackbar.make(findViewById(R.id.loginview), "wrong password", Snackbar.LENGTH_SHORT);
                                sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                                sb.show();
                            } catch (Exception e) {
                                Log.d("MyDebug", e.getMessage());
                            }
                        } else {
                            //update firestore email in case of change
                            DocumentReference userRef = usersRef.document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            userRef.update("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            //continue to main activity
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    /**
     * try to create a firebase auth account with the provided credentials
     */
    private void createAccount() {
        auth.createUserWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        waiting.setVisibility(View.INVISIBLE);
                        if (!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException weakPasswordException) {
                                Log.d("MyDebug", "weak pass");
                                Snackbar sb = Snackbar.make(findViewById(R.id.loginview), "password is too weak", Snackbar.LENGTH_SHORT);
                                sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                                sb.show();
                            } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Log.d("MyDebug", "malformed email");
                                Snackbar sb = Snackbar.make(findViewById(R.id.loginview), "malformed email", Snackbar.LENGTH_SHORT);
                                sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                                sb.show();
                            } catch (Exception e) {
                                Log.d("MyDebug", e.getMessage());
                            }
                        } else {
                            //create new user profile
                            createProfile();
                            //continue to main
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    /**
     * create associated firestore document and update authentication display name
     */
    private void createProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //update display name
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(usernameField.getText().toString())
                .build();
        user.updateProfile(profileChangeRequest);

        //create and store firestore document
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

        //create notification doc for user
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Map<String, Object> newNotifDoc = new HashMap<>();
        newNotifDoc.put("requests", Arrays.asList());
        newNotifDoc.put("date", df.format(d));
        db.collection("notifications").document(user.getUid())
                .set(newNotifDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("NotifDoc", "notif doc created");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("NotifDoc", "failed to create new notif doc");
                    }
                });
    }

    /**
     * starts signup or signin flows based on signin exception using email
     */
    private void determineNewuser() {
        if (emailField.getText().toString().isEmpty()) {
            Snackbar sb = Snackbar.make(findViewById(R.id.loginview), "enter an email", Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
            sb.show();
            waiting.setVisibility(View.INVISIBLE);
            return;
        }
        auth.fetchSignInMethodsForEmail(emailField.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                waiting.setVisibility(View.INVISIBLE);
                if (!task.isSuccessful()) {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException invalidCredentialsException) {
                        Log.d("MyDebug", invalidCredentialsException.getErrorCode());
                        Snackbar sb = Snackbar.make(findViewById(R.id.loginview), "malformed email", Snackbar.LENGTH_SHORT);
                        sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                        sb.show();
                    }
                    catch (Exception e) {
                        Log.d("MyDebug", e.getClass().getName());
                        Snackbar sb = Snackbar.make(findViewById(R.id.loginview), "sign in error", Snackbar.LENGTH_SHORT);
                        sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                        sb.show();
                    }
                } else {
                    if (task.getResult().getSignInMethods().size() == 1) {
                        //account exists, start signin flow
                        Log.d("MyDebug", "proceed with login");
                        passwordField.setVisibility(View.VISIBLE);
                        continueAuthBtn.setEnabled(true);
                        stage = 1;
                    } else {
                        Log.d("MyDebug", "need new account");
                        //This email is not associated with an account
                        //start signup flow
                        Log.d("MyDebug", "need new account");
                        passwordField.setVisibility(View.VISIBLE);
                        usernameField.setVisibility(View.VISIBLE);
                        checkInfoBtn.setVisibility(View.VISIBLE);
                        stage = 2;
                    }
                }
            }
        });
    }

    /**
     * checks that provided username is available and other fields are not empty
     */
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
                        //user does not exist, allow button to continue
                        if (stage == 2 && emailField.getText().toString().length() > 0 && usernameField.getText().toString().length() > 0 && passwordField.getText().toString().length() > 0) {
                            continueAuthBtn.setEnabled(true);
                        } else if (stage == 1 && emailField.getText().toString().length() > 0 && passwordField.getText().toString().length() > 0) {
                            continueAuthBtn.setEnabled(true);
                        }
                    } else {
                        Snackbar sb = Snackbar.make(findViewById(R.id.loginview), "username Taken", Snackbar.LENGTH_LONG);
                        sb.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                        sb.show();
                        continueAuthBtn.setEnabled(false);
                        Log.d("MyDebug", "username not free");
                    }
                } else
                    Log.d("MyDebug", task.getException().toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        //send user back to auth page
        Intent intent = new Intent(getBaseContext(), AuthPage.class);
        startActivity(intent);
        finish();
    }
}