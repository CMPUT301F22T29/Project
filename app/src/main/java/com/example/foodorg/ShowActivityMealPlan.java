package com.example.foodorg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ShowActivityMealPlan extends AppCompatActivity {

    private Button returnMP;
    private FirebaseFirestore Firestoredb;
    private FirebaseAuth FireAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_meal_plan);




        Firestoredb = FirebaseFirestore.getInstance();
        FireAuth = FirebaseAuth.getInstance();
        userID = FireAuth.getCurrentUser().getUid();

        TextView storageItemName,storageItemCategory,storageItemDescription,storageItemBB,storageItemLocation,storageItemAmount,storageItemUnit;

        storageItemName = findViewById(R.id.nameMPIngredientTV);
        storageItemCategory = findViewById(R.id.categoryMPIngredientTV);
        storageItemBB = findViewById(R.id.BBdateMPIngredientTV);
        storageItemLocation = findViewById(R.id.locationMPIngredientTV);
        storageItemAmount = findViewById(R.id.amountMPIngredientTV);
        storageItemUnit = findViewById(R.id.unitMPIngredientTV);



        returnMP = findViewById(R.id.returnButtonMP);
        returnMP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowActivityMealPlan.this, MealPlanActivity.class);
                startActivity(i);

            }
        });

        String validity= getIntent().getStringExtra("nameFind");

        showData(validity);



    }

    private void showData(String findName){

        TextView storageItemName,storageItemCategory,storageItemDescription,storageItemBB,storageItemLocation,storageItemAmount,storageItemUnit;

        storageItemName = findViewById(R.id.nameMPIngredientTV);
        storageItemCategory = findViewById(R.id.categoryMPIngredientTV);
        storageItemBB = findViewById(R.id.BBdateMPIngredientTV);
        storageItemLocation = findViewById(R.id.locationMPIngredientTV);
        storageItemAmount = findViewById(R.id.amountMPIngredientTV);
        storageItemUnit = findViewById(R.id.unitMPIngredientTV);
        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = Firestoredb.collection("users");
        collectionReference.document(userID).collection("Ingredient_Storage")
                .whereEqualTo("name",findName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    /**
                     * onComplete method for the task
                     * @param task which is the task for firestore
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // First clear ingredient list, then add ingredients based on the models as stored in Firestore

                        for (DocumentSnapshot snapshot : task.getResult()){

                            storageItemName.setText(snapshot.getString("name"));
                            storageItemCategory.setText(snapshot.getString("category"));
                            storageItemBB.setText(snapshot.getString("bestBefore"));
                            storageItemLocation.setText(snapshot.getString("location"));
                            storageItemAmount.setText(String.valueOf(snapshot.getLong("amount")));
                            storageItemUnit.setText(String.valueOf(snapshot.getLong("unit").intValue()));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowActivityMealPlan.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}