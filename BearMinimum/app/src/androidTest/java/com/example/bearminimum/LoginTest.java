package com.example.bearminimum;


import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import static android.content.ContentValues.TAG;

/**
 * Test class for LoginActivity. All the UI tests are written here. Robotium test framework is
 used
 */
@RunWith(AndroidJUnit4.class)
public class LoginTest {

    //TODO
    // change user info to appropriate values
    //sample user acc creation data (new user)
    private String newUid = "";
    private String newEmail = "";
    private String newName = "";
    private String newPassword = "";

    //2 sample test users (already in database)
    //sample user1 login info
    private String uid1 = "";
    private String email1 = "";
    private String uName1 = "";
    private String password1 = "";
    //sample user2 login info
    private String uid2 = "";
    private String email2 = "";
    private String uName2 = "";
    private String password2 = "";

    private Solo solo;
    FirebaseFirestore db;


    @Rule
    public ActivityTestRule<AuthPage> rule =
            new ActivityTestRule<>(AuthPage.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
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
     * Creates a new user account
     * Checks if user is now in database
     */
    @Test
    public void createAcc(){
        //asserts that current activity is login activity (LoginActivity) when user opens app.
        solo.assertCurrentActivity("Wrong Activity", AuthPage.class);

        //TODO
        // edit buttons to fit
        // edit EditText views to match

        //click sign up button
        solo.clickOnButton("Sign in");
        solo.waitForActivity("LoginActivity");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        //get views for input fields
        //enter new acc info
        solo.enterText((EditText) solo.getView(R.id.email_box), newEmail);

        //press continue
        solo.clickOnButton("CONTINUE");
        solo.sleep(2000);

        solo.enterText((EditText) solo.getView(R.id.username_box), newUid);
        solo.enterText((EditText) solo.getView(R.id.password_box), newPassword);
        solo.clickOnButton("CHECK");  //check for duplicate accs

        //access Firestore
        db = FirebaseFirestore.getInstance();

        //check if user document exists
        //(https://firebase.google.com/docs/firestore/query-data/get-data#java_2)

        //TODO
        // double check field names are correct when accessing document field values

        DocumentReference docRef =  db.collection("users").document(newUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        //document exists
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        //check if all the info matches
                        String uid = document.getString("uid");
                        String email = document.getString("email");
                        String username = document.getString("username");
                        assertEquals(newUid, uid);
                        assertEquals(newEmail, email);
                        assertEquals(newName, username);

                    } else {
                        Log.d(TAG, "No such document");
                        Assert.fail("Document doesn't exist");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Assert.fail("Failed to get document");
                }
            }
        });


    }

    //TODO
    // check if acc creation will watches for 'unique id' (no 2 user has same id)
    // check if acc creation goes through if nothing entered (empty name, id, etc)


    /**
     * Login with a previously created account
     */
    @Test
    public void login(){
        //asserts that current activity is login activity (LoginActivity) when user opens app.
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        //click sign up button
        solo.clickOnButton("Sign in");
        solo.waitForActivity("LoginActivity");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        //get views for input fields
        //enter login info
        solo.enterText((EditText) solo.getView(R.id.email_box), email1);

        //press continue
        solo.clickOnButton("CONTINUE");
        solo.sleep(2000);

        solo.enterText((EditText) solo.getView(R.id.password_box), password1);
        solo.clickOnButton("CONFIRM");  //confirm info to login

        //check if it navigates to MainActivity after login
        solo.waitForActivity("MainActivity");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        //check if logged in user matches with current user from database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            //TODO
            // double check if .getUid() and getEmail() works
            //if user exists, check info matches
            String userID = user.getUid();
            String email = user.getEmail();

            assertEquals(uid1, userID);
            assertEquals(email1, email);
        }

    }

    //TODO
    // check for error message if user enters wrong password, wrong uid
    // check for error message if user leaves a field blank


}
