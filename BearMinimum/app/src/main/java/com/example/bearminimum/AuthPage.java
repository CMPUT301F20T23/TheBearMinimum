package com.example.bearminimum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

/**
 * AuthPage
 *
 * This class serves as the entry point for to the app for users who aren't logged in
 *
 * Nov.6, 2020
 */
public class AuthPage extends AppCompatActivity {
    //activity result code
    private static final int RC_SIGN_IN = 123;

    //ui elements
    private Button signinBtn;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * creates an intent and returns it to where it was
     * called to start this activity
     *
     * @param context
     * @return          an Intent object
     */

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, AuthPage.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if user signed in previously and hasn't signed out, skip signin
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authpage);

        //start login activity
        signinBtn = findViewById(R.id.button_signin);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //prevent going back once signed out
    }
}