package com.example.bearminimum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SignOutTest {

    //sample user acc info
    private String uid = "";
    private String password = "";

    //firebase
    FirebaseFirestore db;

    //Robotium testing
    private Solo solo;


    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(AuthPage.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * logs in using sample user info
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        //login
        solo.clickOnButton("Sign in");

        //get views for input fields
        //enter sample user login info
        solo.enterText((EditText) solo.getView(R.id.signInUid), uid);
        solo.enterText((EditText) solo.getView(R.id.signInPassword), password);
        solo.clickOnButton("CONFIRM");  //confirm info to login

        //check if it navigates to MainActivity after login
        solo.waitForActivity("MainActivity");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * check if sign out can sign out successfully
     */
    @Test
    public void signOut(){
        //asserts that current activity is MainActivity.
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //TODO
        // modify to fit sign out process as needed
        // double check sign out button

        //log out
        solo.clickOnButton("Log out");

        //check if it navigates back to login page (AuthPage)
        solo.waitForActivity("AuthPage");
        solo.assertCurrentActivity("Wrong Activity", AuthPage.class);

        //check that current user is null
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assertNull(user);

    }
}
