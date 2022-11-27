package com.example.foodorg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 *  HomePageActivity is the first activity a user sees after logging into the app. In
 *  this Activity the user can click on the ImageButtons for the new respective Activities IngredientStorageActivity
 *  and RecipeActivity, to access the functionalities for their respective user story requirements
 *
 * <p>
 * The Activity contains Image Buttons that allow a user to access their Ingredient Storage, or their Recipes.
 * The respective Image Buttons have Textview's below them to also indicate
 * </p>
 *
 * <p>
 * The Activity contains a Textview with the user's username which is the string in their email
 * </p>
 *
 * @author amman1
 * @author mohaimin
 *
 */

public class HomePageActivity extends AppCompatActivity {

    // First initialize the TextViews & the Buttons
    // based on their respective id's for returning to
    // Login Page, Clicking on the Recipe Button or
    // The Ingredient Storage Button
    Button returnLogin;
    ImageButton recipeBtn;
    ImageButton ingredientStorageBtn;
    ImageButton mealPlanBtn;
    ImageButton shoppingListBtn;

    TextView usernameText;
    String userEmail;

    // Initialize the FireBase Authentication
    private FirebaseAuth fireBaseAuthentication;

    /**
     * The onCreate Method
     * @param savedInstanceState Bundle of arguments
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);


        // First initialize the Username Textview & the
        // Different ImageButtons for Ingredient Storage and recipes
        // based on their respective id's, as well as
        // The firebase Authentication
        returnLogin = findViewById(R.id.LogOutButton);
        ingredientStorageBtn = findViewById(R.id.ingredientButtonHome);
        recipeBtn = findViewById(R.id.recipeButtonHome);
        mealPlanBtn =findViewById(R.id.mealPlanButtonHome);
        shoppingListBtn = findViewById(R.id.shoppingListButtonHome);
        fireBaseAuthentication = FirebaseAuth.getInstance();
        usernameText = findViewById(R.id.userName);

        // Get the username from the users email as the first
        // text right before the @ in their email
        userEmail = fireBaseAuthentication.getCurrentUser().getEmail();
        String[] arrOfStr = userEmail.split("@", 2);
        usernameText.setText(arrOfStr[0]);


        // OnClickListener for returning to Login Page
        returnLogin.setOnClickListener(new View.OnClickListener() {
            /**
             * The onCreate Method
             * @param v Where here view is the returnLogin Button
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        // OnClickListener for going to Ingredient Storage Page
        ingredientStorageBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * The onCreate Method
             * @param v Where here view is the ingredientStorageBtn ImageButton
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePageActivity.this, IngredientStorageActivity.class);
                i.putExtra("key","1");
                startActivity(i);
            }
        });


        // OnClickListener for going to Recipe Page
        recipeBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * The onCreate Method
             * @param v Where here view is the recipeBtn ImageButton
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePageActivity.this, RecipeActivity.class);
                i.putExtra("key","1");
                startActivity(i);
            }
        });

        // OnClickListener for going to MealPlan Page
        mealPlanBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * The onCreate Method
             * @param v Where here view is the mealPlanBtn ImageButton
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePageActivity.this, MealPlanActivity.class);
                startActivity(i);
            }
        });

        // OnClickListener for going to Shopping List Page
        shoppingListBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * The onCreate Method
             * @param view Where here view is the recipeBtn ImageButton
             */
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomePageActivity.this, ShoppingListActivity.class);
                startActivity(i);
            }
        });

    }
}