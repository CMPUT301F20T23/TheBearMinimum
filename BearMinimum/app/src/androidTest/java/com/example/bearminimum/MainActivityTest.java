package com.example.bearminimum;

import android.graphics.Point;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MainActivityTest {
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

    @Test
    public void AddBookByISBN (){
        solo.assertCurrentActivity("Wrong activity",MainActivity.class);
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("add book");
        solo.clickOnButton("add by ISBN");
        solo.enterText((EditText) solo.getView(R.id.ISBNSearch),"9781443411080");
        solo.clickOnButton("search");
        solo.waitForText("Year Book",1,2000);
        solo.clickOnButton("add");
        assertTrue(solo.searchText("The Great Gatsby"));

    }

    @Test
    public void AddBookTest (){
        solo.assertCurrentActivity("Wrong activity",MainActivity.class);

        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("add book");
        solo.enterText((EditText) solo.getView(R.id.editTitle),"Year Book");
        solo.enterText((EditText) solo.getView(R.id.editAuthor),"World Book Encyclopedia");
        solo.enterText((EditText) solo.getView(R.id.editISBN),"0716604892");
        solo.enterText((EditText) solo.getView(R.id.editDescr),"The Annual Supplement to the World Book Encyclopedia : the 1989 World Book : a Review of the Events of 1988");
        Button MenuButton = solo.getButton("add");
        solo.clickOnView(MenuButton);
        solo.waitForText("Year Book",1,2000);

        assertTrue(solo.searchText("Year Book"));


    }

    /**
     * verify info on current user
     */
    @Test
    public void checkCurrentUserProfile() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("profile");

        assertTrue(solo.waitForActivity(ProfileActivity.class,2000));
        assertTrue(solo.waitForText(user.getEmail(),1,2000));
        assertTrue(solo.waitForText(user.getDisplayName(),1,2000));
    }

    /**
     * check navigation to notifications page
     */
    @Test
    public void navToNotificationsActivity() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("notifications");

        assertTrue(solo.waitForActivity(ViewNotificationsActivity.class,2000));
        assertTrue(solo.waitForText("notification", 1, 2000));
    }

    /**
     * check navigation to incoming requests page
     */
    @Test
    public void navToIncomingReqActivity() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("incoming requests");

        assertTrue(solo.waitForActivity(IncomingReqs.class,2000));
        assertTrue(solo.waitForText("books with requests", 1, 2000));
    }

    /**
     * check navigation to outgoing requests page
     */
    @Test
    public void navToOutgoingReqsActivity() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("outgoing requests");

        assertTrue(solo.waitForActivity(OutgoingReqsActivity.class,2000));
        assertTrue(solo.waitForText("outgoing requests", 1, 2000));
    }

    /**
     * check navigation to accepted incoming requests
     */
    @Test
    public void navToAcceptedIncomingReqsActivity() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("accepted incoming requests");

        assertTrue(solo.waitForActivity(AcceptedIncomingReqs.class,2000));
        assertTrue(solo.waitForText("accepted incoming requests", 1, 2000));
    }

    /**
     * check navigation to accepted outgoing requests
     */
    @Test
    public void navToAcceptedOutgoingReqsActivity() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("accepted outgoing requests");

        assertTrue(solo.waitForActivity(AcceptedOutgoingReqs.class,2000));
        assertTrue(solo.waitForText("accepted outgoing requests", 1, 2000));
    }

    /**
     * check navigation to scan book activity
     */
    @Test
    public void navToScanBookActivity() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("scan book");

        assertTrue(solo.waitForActivity(ScanBook.class,2000));
        assertTrue(solo.waitForText("scan by ISBN", 1, 2000));
        assertTrue(solo.waitForText("enter ISBN number", 1, 2000));
        assertTrue(solo.waitForText("yes", 1, 2000));
        assertTrue(solo.waitForText("scan", 1, 2000));
    }

    /**
     * check navigation to search activity
     */
    @Test
    public void navToSearchActivity() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //nav to notifications page
        solo.clickOnImageButton(0); // open menu
        solo.clickOnText("search");

        assertTrue(solo.waitForActivity(SearchActivity.class,2000));
        assertTrue(solo.waitForText("enter your search", 1, 2000));
        assertTrue(solo.waitForText("books",1, 2000));
        assertTrue(solo.waitForText("users", 1, 2000));
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
