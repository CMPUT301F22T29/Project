package com.example.foodorg;


import static org.junit.Assert.assertEquals;

import android.app.Activity;
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
public class RecipeActivityTest {

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
    public void goToRecipe(){
// Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity” 
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.enterText((EditText) solo.getView(R.id.emailLoginMain), "l@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordLoginMain), "111111");

        solo.clickOnButton("LOGIN"); //Select ClEAR ALL

        solo.waitForActivity("HomePageActivity");
        solo.assertCurrentActivity("Wrong Activity", HomePageActivity.class);

        solo.clickOnView(solo.getView(R.id.recipeButtonHome));
        solo.waitForActivity("RecipeActivity");

        solo.waitForActivity(RecipeActivity.class, 1000);

        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);

    }


    @Test
    public void addIncorrectRecipe(){

        goToRecipe();
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);

        solo.clickOnView(solo.getView(R.id.AddButtonRecipe));
        solo.waitForDialogToOpen();

        RecyclerView recyclerIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);
        int x = recyclerIS.getAdapter().getItemCount();

//        solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), "2");
        //solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), String.valueOf(x));

        solo.clickOnView(solo.getView(R.id.btnRecipeEdit));

        solo.waitForActivity(RecipeActivity.class, 1000);

        int y = recyclerIS.getAdapter().getItemCount();
        assertEquals(x, y);

    }

    @Test
    public void addCorrectRecipe(){

        goToRecipe();
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);

        solo.clickOnView(solo.getView(R.id.AddButtonRecipe));
        solo.waitForDialogToOpen();

        RecyclerView recyclerIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);
        int x = recyclerIS.getAdapter().getItemCount();

//        solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), "2");

        solo.enterText((EditText) solo.getView(R.id.title_recipe_input), "Biryani");
        solo.enterText((EditText) solo.getView(R.id.category_recipe_input), "Indian Dish");
        solo.enterText((EditText) solo.getView(R.id.time_recipe_input), "2");
        solo.enterText((EditText) solo.getView(R.id.servings_recipe_input), "2");
        solo.enterText((EditText) solo.getView(R.id.comment_recipe_input), "Very Spicy");

        // solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), String.valueOf(x));

        solo.clickOnView(solo.getView(R.id.btnRecipeEdit));
        solo.waitForDialogToClose();

        solo.waitForActivity("IngredientOfRecipeActivity");

        solo.clickOnView(solo.getView(R.id.returnButtonIngredientStorage));

        solo.waitForActivity("RecipeActivity");

        RecyclerView recyclerAfterIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);
        int y = recyclerAfterIS.getAdapter().getItemCount();
        assertEquals(x+1, y);

    }

    @Test
    public void addDeleteRecipe() throws InterruptedException {

        addCorrectRecipe();

        solo.waitForActivity(RecipeActivity.class);
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);

        RecyclerView recyclerBeforeIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);
        int m = recyclerBeforeIS.getAdapter().getItemCount();

        LinearLayout layout = recyclerBeforeIS.getLayoutManager().findViewByPosition(0).findViewById(R.id.recipeItemAdapter);

        solo.clickOnView(layout);
        solo.waitForActivity(RecipeActivity.class, 1000);


        Button delete = (Button) layout.findViewById(R.id.deleteRecipe);

        solo.clickOnButton(String.valueOf(delete.getText().toString()));

        //layout.setVisibility(View.VISIBLE);

        solo.waitForActivity(RecipeActivity.class, 2000);

        RecyclerView recyclerAfterIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);
        int n = recyclerAfterIS.getAdapter().getItemCount();
        assertEquals(m-1, n);


    }

    @Test
    public void photoButton(){

        addCorrectRecipe();

        solo.waitForActivity(RecipeActivity.class);
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);

        RecyclerView recyclerBeforeIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);
        int m = recyclerBeforeIS.getAdapter().getItemCount();

        LinearLayout layout = recyclerBeforeIS.getLayoutManager().findViewByPosition(0).findViewById(R.id.recipeItemAdapter);

        solo.clickOnView(layout);
        solo.waitForActivity(RecipeActivity.class, 1000);


        Button photo = (Button) layout.findViewById(R.id.cameraBtn);

        solo.clickOnButton(String.valueOf(photo.getText().toString()));

        solo.waitForActivity(CameraActivity.class, 3000);

        solo.assertCurrentActivity("Wrong Activity", CameraActivity.class);

    }

    @Test
    public void editRecipe() {

        addCorrectRecipe();


        RecyclerView recyclerBeforeIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);

        LinearLayout layout = recyclerBeforeIS.getLayoutManager().findViewByPosition(0).findViewById(R.id.recipeItemAdapter);

        solo.clickOnView(layout);


        Button edit = (Button) layout.findViewById(R.id.editRecipe);

        solo.clickOnButton(String.valueOf(edit.getText().toString()));


        solo.clearEditText((EditText) solo.getView(R.id.title_recipe_input));
        solo.clearEditText((EditText) solo.getView(R.id.category_recipe_input));
        solo.clearEditText((EditText) solo.getView(R.id.time_recipe_input));
        solo.clearEditText((EditText) solo.getView(R.id.servings_recipe_input));
        solo.clearEditText((EditText) solo.getView(R.id.comment_recipe_input));

        solo.enterText((EditText) solo.getView(R.id.title_recipe_input), "Pakora");
        solo.enterText((EditText) solo.getView(R.id.category_recipe_input), "Indian Dish");
        solo.enterText((EditText) solo.getView(R.id.time_recipe_input), "2");
        solo.enterText((EditText) solo.getView(R.id.servings_recipe_input), "2");
        solo.enterText((EditText) solo.getView(R.id.comment_recipe_input), "Very Spicy");

        // solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), String.valueOf(x));

        solo.clickOnView(solo.getView(R.id.btnRecipeEdit));


//        RecyclerView recyclerAfterIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);
//
//
//        LinearLayout layout1 = recyclerAfterIS.getLayoutManager().findViewByPosition(0).findViewById(R.id.recipeItemAdapter);
//
//        solo.clickOnView(layout1);
//        solo.waitForActivity(RecipeActivity.class, 500);
//
//
//        Button ingredientsRecipe = (Button) layout.findViewById(R.id.ingredientsRecipeCardBtn);
//
//        solo.clickOnButton(String.valueOf(ingredientsRecipe.getText().toString()));
//        solo.waitForActivity("IngredientOfRecipeActivity");
//
//        TextView prep = (TextView) solo.getView(R.id.recipe_tile);
//
//        assertEquals("Pakora", prep.getText().toString());

        // solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), String.valueOf(x));
//
//        solo.clickOnView(solo.getView(R.id.btnRecipeEdit));
//        solo.waitForDialogToClose();
//
//        solo.waitForActivity("IngredientOfRecipeActivity", 500);
//
//        solo.assertCurrentActivity("Wrong Activity", IngredientOfRecipeActivity.class);



    }

    @Test
    public void testEditRecipe(){
        editRecipe();


        RecyclerView recyclerBeforeIS = (RecyclerView) solo.getView(R.id.RecipeListRecyclerView);


        LinearLayout layout = recyclerBeforeIS.getLayoutManager().findViewByPosition(0).findViewById(R.id.recipeItemAdapter);
//
//        solo.clickOnView(layout);
//        solo.waitForActivity(RecipeActivity.class, 1000);


        Button photo = (Button) layout.findViewById(R.id.ingredientsRecipeCardBtn);

        solo.clickOnButton(String.valueOf(photo.getText().toString()));

        solo.waitForActivity(IngredientOfRecipeActivity.class, 3000);

        solo.assertCurrentActivity("Wrong Activity", IngredientOfRecipeActivity.class);

    }

    @Test
    public void addIngredient(){
        goToRecipe();
        solo.assertCurrentActivity("Wrong Activity", RecipeActivity.class);

        solo.clickOnView(solo.getView(R.id.AddButtonRecipe));
        solo.waitForDialogToOpen();


//        solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), "2");

        solo.enterText((EditText) solo.getView(R.id.title_recipe_input), "Biryani");
        solo.enterText((EditText) solo.getView(R.id.category_recipe_input), "Indian Dish");
        solo.enterText((EditText) solo.getView(R.id.time_recipe_input), "2");
        solo.enterText((EditText) solo.getView(R.id.servings_recipe_input), "2");
        solo.enterText((EditText) solo.getView(R.id.comment_recipe_input), "Very Spicy");

        // solo.typeText((EditText) solo.getView(R.id.inputUnitAddIStorage), String.valueOf(x));

        solo.clickOnView(solo.getView(R.id.btnRecipeEdit));
        solo.waitForDialogToClose();

        solo.waitForActivity("IngredientOfRecipeActivity");

        RecyclerView recyclernext = (RecyclerView) solo.getView(R.id.ingredientOnlyListRecyclerView);
        int l = recyclernext.getAdapter().getItemCount();

        solo.clickOnView(solo.getView(R.id.AddButtonIngredientStorage));
        solo.waitForDialogToOpen();

        solo.enterText((EditText) solo.getView(R.id.item_input), "Chicken");
        solo.enterText((EditText) solo.getView(R.id.category_input), "Poultry");
        solo.enterText((EditText) solo.getView(R.id.amount_input), "2");
        solo.enterText((EditText) solo.getView(R.id.unit_input), "2");

        solo.clickOnView(solo.getView(R.id.btnEdit));

        solo.waitForActivity("IngredientOfRecipeActivity");

        solo.assertCurrentActivity("Wrong Activity", IngredientOfRecipeActivity.class);

        RecyclerView recyclernextt = (RecyclerView) solo.getView(R.id.ingredientOnlyListRecyclerView);
        int m = recyclernextt.getAdapter().getItemCount();
        assertEquals(l, m);

    }

    @Test
    public void editIngredientOfRecipe(){

        addIngredient();


    }

}
