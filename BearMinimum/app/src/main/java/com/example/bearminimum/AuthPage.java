package com.example.bearminimum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

public class AuthPage extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private Button signinBtn;
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
    protected void onResume() {
        super.onResume();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && getIntent().getExtras() == null) {
            startActivity(MainActivity.createIntent(this, null));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //prevent going back
        /*finish();
        System.exit(0);

         */
    }
}