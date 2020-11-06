package com.example.bearminimum;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LoginActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkSignin() {
        // Asserts that the current activity is the login activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.email_box), "test@bearmin.com");
        solo.clickOnButton("continue");

        assertTrue(solo.waitForText("password",1,2000));
        solo.enterText((EditText) solo.getView(R.id.password_box), "test123");
        solo.clickOnButton("continue");

        assertTrue(solo.waitForActivity(MainActivity.class, 2000));

        solo.clickOnImageButton(0);
        solo.clickOnText("sign out");

        assertTrue(solo.waitForActivity(AuthPage.class, 2000));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
