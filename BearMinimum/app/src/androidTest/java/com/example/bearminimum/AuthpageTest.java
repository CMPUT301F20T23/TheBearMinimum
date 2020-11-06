package com.example.bearminimum;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AuthpageTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<AuthPage> rule = new ActivityTestRule<>(AuthPage.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gests the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkSignInFlow() {
        // asserts that the current activity is Authpage
        solo.assertCurrentActivity("Wrong Activity", AuthPage.class);

        solo.clickOnButton("sign in");

        assertTrue(solo.waitForActivity(LoginActivity.class, 2000));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
