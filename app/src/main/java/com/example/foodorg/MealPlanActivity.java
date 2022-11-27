package com.example.foodorg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MealPlanActivity extends AppCompatActivity {

    Button returnHomeFromMeal;
    Button addBYRecipeBtn;
    Button addBYIngredientBtn;
    private FirebaseFirestore Firestoredb;

    private RecyclerView mealPlanRecyclerView;
    private MealPlanAdapter mealPlanAdapter;
    private List<MealPlanModel> mealPlanModelList;
    private FirebaseAuth FireAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_plan_page);


        // Initialize the returnHomeButton
        returnHomeFromMeal = findViewById(R.id.returnButtonIngredientStorage);
        returnHomeFromMeal.setOnClickListener(new View.OnClickListener() {
            /**
             * @param v view
             */
            @Override
            public void onClick(View v) {
                // Start HomePage Activity
                Intent i = new Intent(MealPlanActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });


        addBYIngredientBtn = findViewById(R.id.addBtnByIngredient);
        addBYIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MealPlanActivity.this, IngredientStorageActivity.class);
                i.putExtra("key","2");
                startActivity(i);
            }
        });

        addBYRecipeBtn = findViewById(R.id.addBtnByRecipe);
        addBYRecipeBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * @param v view
             */
            @Override
            public void onClick(View v) {
                // Start HomePage Activity
                Intent i = new Intent(MealPlanActivity.this, RecipeActivity.class);
                i.putExtra("key","0");
                startActivity(i);
            }
        });

        mealPlanRecyclerView = findViewById(R.id.mealPlanRecyclerView);
        mealPlanRecyclerView.setLayoutManager(new LinearLayoutManager(this));

// initialize the instance of the firestore database
        Firestoredb = FirebaseFirestore.getInstance();
        FireAuth = FirebaseAuth.getInstance();

// initialize the model list of the ingredients, as well as the adapter for it and set it
        mealPlanModelList = new ArrayList<>();
        mealPlanAdapter = new MealPlanAdapter(this, mealPlanModelList);
        mealPlanRecyclerView.setAdapter(mealPlanAdapter);


        showData();



    }

    private void showData(){

        userID = FireAuth.getCurrentUser().getUid();
        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = Firestoredb.collection("users");
        collectionReference.document(userID).collection("MealPlan")
                .orderBy("name")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    /**
                     * onComplete method for the task
                     * @param task which is the task for firestore
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // First clear ingredient list, then add ingredients based on the models as stored in Firestore
                        mealPlanModelList.clear();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            MealPlanModel mealPlanModel = new MealPlanModel(snapshot.getString("name"), snapshot.getString("date"),
                                    snapshot.getString("mealID"), snapshot.getString("servings"),snapshot.getLong("whichStore").intValue());
                            mealPlanModelList.add(mealPlanModel);
                        }
                        // Final update to let Adapter know dataset changed
                        mealPlanAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MealPlanActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }



}