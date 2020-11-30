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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RequestBookTest {
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
     * test requesting a book
     * check for the requested book in outgoing request activity
     */
    @Test
    public void checkRequestBook() {
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

        //click on book and request it
        solo.clickOnText("The Outsider");
        //wait for the activity to show
        assertTrue(solo.waitForActivity(ViewBookActivity.class, 2000));
        assertTrue(solo.waitForText("request book", 1, 2000));
        solo.clickOnButton("request book");
        assertTrue(solo.waitForText("withdraw request", 1, 2000));

        //check if book exists in outgoing request activity
        solo.goBack();
        assertTrue(solo.waitForActivity(SearchActivity.class, 2000));
        solo.goBack();
        assertTrue(solo.waitForActivity(MainActivity.class, 2000));

        //nav to outgoing request activity page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("outgoing requests");

        //check for book
        assertTrue(solo.waitForActivity(OutgoingReqsActivity.class, 2000));
        assertTrue(solo.waitForText("The Outsider", 1, 2000));
        assertTrue(solo.waitForText("ham", 1, 2000));
    }

    /**
     * test withdrawing a request
     * check if book exists in outgoing request activity
     */
    @Test
    public void checkWithdrawRequestBook() {
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

        //click on book and request it
        solo.clickOnText("The Outsider");
        //wait for the activity to show
        assertTrue(solo.waitForActivity(ViewBookActivity.class, 2000));
        assertTrue(solo.waitForText("withdraw request", 1, 2000));
        solo.clickOnButton("withdraw request");
        assertTrue(solo.waitForText("request book", 1, 2000));

        //check if book exists in outgoing request activity
        solo.goBack();
        assertTrue(solo.waitForActivity(SearchActivity.class, 2000));
        solo.goBack();
        assertTrue(solo.waitForActivity(MainActivity.class, 2000));

        //nav to outgoing request activity page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("outgoing requests");

        //check for book
        assertTrue(solo.waitForActivity(OutgoingReqsActivity.class, 2000));
        assertFalse(solo.waitForText("The Outsider", 1, 2000));
        assertFalse(solo.waitForText("ham", 1, 2000));
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
