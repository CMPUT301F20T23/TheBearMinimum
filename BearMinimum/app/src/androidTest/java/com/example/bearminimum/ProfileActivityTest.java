package com.example.bearminimum;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProfileActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<ProfileActivity> rule = new ActivityTestRule<>(ProfileActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword("test@bearmin.com", "test123");
        }
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     *
     */
    @Test
    public void checkCurrentUserProfile() {
        // Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity”
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assertTrue(solo.waitForText(user.getEmail(),1,2000));
        assertTrue(solo.waitForText(user.getDisplayName(),1,2000));
    }

    @After
    public void tearDown() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            FirebaseAuth.getInstance().signOut();
        solo.finishOpenedActivities();
    }
}
