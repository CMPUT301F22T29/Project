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
/**
 * ShowActivityMealPLan to view Ingredients from meal plan
 * <ul>
 *     <li>Add button Ingredient Storage</li>
 *     <li>Add button Recipe</li>
 *     <li>Return to HomePage Activity Button</li>
 *     <li>Recyclerview for the mealplan</li>
 * </ul>
 * @author amman1
 * @author mohaimin
 */
public class ShowActivityMealPlan extends AppCompatActivity {

    private Button returnMP;
    private FirebaseFirestore Firestoredb;
    private FirebaseAuth FireAuth;
    private String userID;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_meal_plan);




        Firestoredb = FirebaseFirestore.getInstance();
        FireAuth = FirebaseAuth.getInstance();
        userID = FireAuth.getCurrentUser().getUid();



        returnMP = findViewById(R.id.returnButtonMP);
        returnMP.setOnClickListener(new View.OnClickListener() {
            /**
             * return to Meal Plan
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowActivityMealPlan.this, MealPlanActivity.class);
                startActivity(i);

            }
        });

        String validity= getIntent().getStringExtra("nameFind");

        String id = getIntent().getStringExtra("idmeal");

        showData(id, validity);



    }

    /**
     * Show the data of the ingredient scaled
     * @param id
     * @param validity
     */
    private void showData(String id, String validity){

        TextView storageItemName,storageItemCategory,storageItemDescription,storageItemBB,storageItemLocation,storageItemAmount,storageItemUnit;

        storageItemName = findViewById(R.id.nameMPIngredientTV);
        storageItemCategory = findViewById(R.id.categoryMPIngredientTV);
        storageItemBB = findViewById(R.id.BBdateMPIngredientTV);
        storageItemLocation = findViewById(R.id.locationMPIngredientTV);
        storageItemAmount = findViewById(R.id.amountMPIngredientTV);
        storageItemUnit = findViewById(R.id.unitMPIngredientTV);
        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = Firestoredb.collection("users");



        collectionReference.document(userID).collection("Relationship")
                .whereEqualTo("mealID",id)
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

                            if (snapshot.getString("description").equals(validity)){

                                storageItemName.setText(snapshot.getString("description"));
                                storageItemCategory.setText(snapshot.getString("category"));
                                storageItemBB.setText(snapshot.getString("bb"));
                                storageItemLocation.setText(snapshot.getString("location"));
                                storageItemAmount.setText(String.valueOf(snapshot.get("amount")));
                                storageItemUnit.setText(String.valueOf(snapshot.get("unit")));

                            }


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