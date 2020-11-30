package com.example.bearminimum;

import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;

public class SearchActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<MainActivity>(MainActivity.class, true, true){
                @Override
                protected void beforeActivityLaunched() {
                    Log.d("TestDebug", "before main activity");
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword("test@bearmin.com", "test123").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                    Log.d("TestDebug", "logged in");
                                else
                                    Log.d("TestDebug", task.getException().getMessage());
                            }
                        });
                        SystemClock.sleep(3000);
                    }
                }
            };

    @Before
    public void setUp(){
        solo = new Solo(getInstrumentation(),rule.getActivity());
    }

    /**
     * test search for a specific book
     *      searching for outsider
     */
    @Test
    public void searchBook() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("search");

        assertTrue(solo.waitForActivity(SearchActivity.class,2000));
        assertTrue(solo.waitForText("enter your search", 1, 2000));

        //type in the search
        solo.enterText(0, "outsider");

        //assert book found
        assertTrue(solo.waitForText("The Outsider", 1, 2000));
        assertTrue(solo.waitForText("ham", 1, 2000));
    }

    /**
     * test search for a specific user
     *      searching for ham
     */
    @Test
    public void searchUser() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("search");

        assertTrue(solo.waitForActivity(SearchActivity.class,2000));
        assertTrue(solo.waitForText("enter your search", 1, 2000));

        //change to search for user
        solo.clickOnButton("users");

        //type in the search
        solo.enterText(0, "ham");

        //assert user found
        assertTrue(solo.waitForText("ham", 1, 2000));
    }

    /**
     * logout and finish
     */
    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
        solo.finishOpenedActivities();
    }

}
