package com.example.foodorg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IngredientActivity extends AppCompatActivity {

    Button returnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient);

        returnHome = findViewById(R.id.returnButtonIngredientStorage);
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IngredientActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });
    }
}