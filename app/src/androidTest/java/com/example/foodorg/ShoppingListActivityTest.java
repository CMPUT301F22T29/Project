package com.example.foodorg;


import android.app.Activity;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ShoppingListActivityTest {

    private Solo solo;
    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }
    /**
     * Check the current LoginActivity using assertTrue
     * Check for New Activity RegistrationActivity with assertFalse
     */
    @Test
    public void goToShoppingList(){
// Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity” 
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailLoginMain), "l@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordLoginMain), "111111");

        solo.clickOnButton("LOGIN"); //Select ClEAR ALL

        solo.waitForActivity("HomePageActivity");
        solo.assertCurrentActivity("Wrong Activity", HomePageActivity.class);

        solo.clickOnView(solo.getView(R.id.shoppingListButtonHome));
        solo.waitForActivity("ShoppingListActivity");


        solo.assertCurrentActivity("Wrong Activity", ShoppingListActivity.class);

    }
}
