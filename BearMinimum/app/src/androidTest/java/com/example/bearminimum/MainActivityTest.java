package com.example.bearminimum;

import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

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
            new ActivityTestRule<>(MainActivity.class, true, true);

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
     * logout and finish
     */
    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }


}
