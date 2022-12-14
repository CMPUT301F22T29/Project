package com.example.foodorg;


import android.app.Activity;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {

    private Solo solo;
    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

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
     * Initialize Register Activity
     */
    @Test
    public void startRegister(){

        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("REGISTER"); //Select ClEAR ALL

        solo.waitForActivity("RegistrationActivity");
        solo.assertCurrentActivity("Wrong Activity", RegistrationActivity.class);

    }

    /**
     * Check the current LoginActivity using assertTrue
     * Check for New Activity RegistrationActivity with assertFalse
     */
    @Test
    public void goToLogin(){
// Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity” 

        startRegister();

        solo.clickOnView(solo.getView(R.id.returnToMainFromRegister));
        solo.waitForActivity("LoginActivity");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

    }


    /**
     * Check the current LoginActivity using assertTrue
     * Enter the Incorrect new Email & Password into the respective EditText's with enterText
     * Check for Incorrect New Activity LoginActivity with assertFalse
     */
    @Test
    public void checkRegisterFalse(){
// Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity” 

        startRegister();

        solo.assertCurrentActivity("Wrong Activity", RegistrationActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailRegister), "");
        solo.enterText((EditText) solo.getView(R.id.passwordRegister), "");
        solo.enterText((EditText) solo.getView(R.id.passwordRegister), "");

        solo.clickOnButton("REGISTER"); //Select ClEAR ALL

        solo.assertCurrentActivity("Wrong Activity", RegistrationActivity.class);

    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

}
