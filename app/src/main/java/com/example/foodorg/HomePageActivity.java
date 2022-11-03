package com.example.foodorg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity {

    Button returnLogin;
    ImageButton recipeBtn;
    ImageButton ingredientStorageBtn;

    TextView usernameText;
    String userEmail;
    private FirebaseAuth fireBaseAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        returnLogin = findViewById(R.id.LogOutButton);
        ingredientStorageBtn = findViewById(R.id.ingredientButtonHome);
        recipeBtn = findViewById(R.id.recipeButtonHome);

        fireBaseAuthentication = FirebaseAuth.getInstance();
        usernameText = findViewById(R.id.userName);
        userEmail = fireBaseAuthentication.getCurrentUser().getEmail();
        String[] arrOfStr = userEmail.split("@", 2);
        usernameText.setText(arrOfStr[0]);


        returnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        ingredientStorageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePageActivity.this, IngredientStorageActivity.class);
                startActivity(i);
            }
        });

        recipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePageActivity.this, RecipeActivity.class);
                startActivity(i);
            }
        });

    }
}