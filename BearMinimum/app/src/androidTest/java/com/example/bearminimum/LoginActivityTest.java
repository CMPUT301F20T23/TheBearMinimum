package com.example.bearminimum;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
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

import static org.junit.Assert.assertTrue;

public class LoginActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<LoginActivity>(LoginActivity.class, true, true) {
        @Override
        protected void beforeActivityLaunched() {
            Log.d("TestDebug", "before main activity");
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                SystemClock.sleep(3000);
            }
        }
    };

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

        assertTrue(solo.waitForText("password",1,10000));
        solo.enterText((EditText) solo.getView(R.id.password_box), "test123");
        solo.clickOnButton("continue");

        assertTrue(solo.waitForActivity(MainActivity.class, 10000));

        solo.clickOnImageButton(0);
        solo.clickOnText("sign out");

        assertTrue(solo.waitForActivity(AuthPage.class, 10000));
    }

    @Test
    public void checkSignup() {
        // Asserts that the current activity is the login activity
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.email_box), "DNE@bearmin.com");
        solo.clickOnButton("continue");

        assertTrue(solo.waitForText("username",1,2000));
        assertTrue(solo.waitForText("password",1,2000));
        assertTrue(solo.waitForText("check",1,2000));
    }

    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
