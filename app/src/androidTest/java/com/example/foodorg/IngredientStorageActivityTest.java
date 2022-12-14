package com.example.foodorg;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IngredientStorageActivityTest {

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
     * Check the current LoginActivity using assertTrue
     * Check for New Activity RegistrationActivity with assertFalse
     */
    @Test
    public void goToIngredientStorage(){
// Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity” 
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailLoginMain), "l@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordLoginMain), "111111");

        solo.clickOnButton("LOGIN"); //Select ClEAR ALL

        solo.waitForActivity("HomePageActivity");
        solo.assertCurrentActivity("Wrong Activity", HomePageActivity.class);

        solo.clickOnView(solo.getView(R.id.ingredientButtonHome));
        solo.waitForActivity("IngredientStorageActivity");
        solo.assertCurrentActivity("Wrong Activity", IngredientStorageActivity.class);

    }


    @Test
    public void addIncorrectIngredient(){

        goToIngredientStorage();
        solo.assertCurrentActivity("Wrong Activity", IngredientStorageActivity.class);

        solo.clickOnView(solo.getView(R.id.AddButtonIngredientStorage));
        solo.waitForDialogToOpen();

        RecyclerView recyclerIS = (RecyclerView) solo.getView(R.id.ingredientListRecyclerView);
        int x = recyclerIS.getAdapter().getItemCount();

//        solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), "2");
        //solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), String.valueOf(x));

        solo.clickOnView(solo.getView(R.id.btnAddIStorageItem));

        int y = recyclerIS.getAdapter().getItemCount();
        assertEquals(x, y);
        solo.waitForActivity("IngredientStorageActivity");

    }

    @Test
    public void addCorrectIngredient(){

        goToIngredientStorage();
        solo.assertCurrentActivity("Wrong Activity", IngredientStorageActivity.class);

        solo.clickOnView(solo.getView(R.id.AddButtonIngredientStorage));
        solo.waitForDialogToOpen();

        RecyclerView recyclerBeforeIS = (RecyclerView) solo.getView(R.id.ingredientListRecyclerView);
        int x = recyclerBeforeIS.getAdapter().getItemCount();

//        solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), "2");

        solo.enterText((EditText) solo.getView(R.id.nameInputAddIStorageItem), "Olive");
        solo.enterText((EditText) solo.getView(R.id.inputDescriptionIStorage), "Black Olive");
        solo.enterText((EditText) solo.getView(R.id.inputLocationAddIStorage), "Fridge");
        solo.enterText((EditText) solo.getView(R.id.inputAmountAddIStorage), "2");
        solo.enterText((EditText) solo.getView(R.id.inputUnitAddIStorage), "3");
        solo.enterText((EditText) solo.getView(R.id.categoryInputAddIStorageItem), "Fruit");

       // solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), String.valueOf(x));

        solo.clickOnView(solo.getView(R.id.btnAddIStorageItem));
        solo.waitForDialogToClose();

        solo.waitForActivity("IngredientStorageActivity");

        RecyclerView recyclerAfterIS = (RecyclerView) solo.getView(R.id.ingredientListRecyclerView);
        int y = recyclerAfterIS.getAdapter().getItemCount();
        assertEquals(x+1, y);

    }

    @Test
    public void addDeleteIngredient() throws InterruptedException {
        addCorrectIngredient();

        solo.waitForActivity(IngredientStorageActivity.class);
        solo.assertCurrentActivity("Wrong Activity", IngredientStorageActivity.class);

        RecyclerView recyclerBeforeIS = (RecyclerView) solo.getView(R.id.ingredientListRecyclerView);
        int m = recyclerBeforeIS.getAdapter().getItemCount();

        LinearLayout layout = recyclerBeforeIS.getLayoutManager().findViewByPosition(0).findViewById(R.id.ingredientStorageExpandable);

        solo.clickOnView(layout);

        solo.waitForActivity(IngredientStorageActivity.class, 1000);


        Button delete = (Button) layout.findViewById(R.id.deleteIngredient);

        solo.clickOnButton(String.valueOf(delete.getText().toString()));

        //layout.setVisibility(View.VISIBLE);

        solo.waitForActivity(IngredientStorageActivity.class, 2000);

        RecyclerView recyclerAfterIS = (RecyclerView) solo.getView(R.id.ingredientListRecyclerView);
        int n = recyclerAfterIS.getAdapter().getItemCount();
        assertEquals(m, n);



    }

}
