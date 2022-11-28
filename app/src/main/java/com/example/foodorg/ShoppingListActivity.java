package com.example.foodorg;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * ShoppingListActivity is the activity the user access to view ingredients in
 * the shopping list. The activity contains
 * <ul>
 *     <li>Return to HomePage Activity Button</li>
 *     <li>Recyclerview for the ingredients needed</li>
 * </ul>
 * @author amman1
 * @author mohaimin
 */
public class ShoppingListActivity extends AppCompatActivity {

    // Button to return to homepage
    private Button returnHome;

    // Recyclerview that stores items of shopping list,
    // along with needed adapter and model list
    private RecyclerView ShoppingListItemsRecyclerView;;
    private ShoppingListIngredientAdapter shoppingListIngredientAdapter;
    private List<ShoppingListIngredientModel> ShoppingListIngredientModelList;

    // Firestore references
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the contentview
        setContentView(R.layout.shoppinglistpage);

        // Initialize firebase authentication and get the current user
        // and userID of the user
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        // Initialize the returnHomeButton
        returnHome = findViewById(R.id.returnHomePageShoppingList);
        returnHome.setOnClickListener(new View.OnClickListener() {
            /**
             * @param view view
             */
            @Override
            public void onClick(View view) {
                // Start HomePage Activity
                Intent i = new Intent(ShoppingListActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });


        // Initialize recyclerview
        ShoppingListItemsRecyclerView = findViewById(R.id.ShoppingListRecyclerView);
        ShoppingListItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the instance of the firestore database
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // initialize the model list of the ingredients, as well as the adapter for it and set it
        ShoppingListIngredientModelList = new ArrayList<>();
        shoppingListIngredientAdapter = new ShoppingListIngredientAdapter(this, ShoppingListIngredientModelList);
        ShoppingListItemsRecyclerView.setAdapter(shoppingListIngredientAdapter);

        // showData finally puts all the ingredients into the model list, and then shows the data
        showShoppingList();

    }


    /**
     * showShoppingList() is function that shows data
     */
    private void showShoppingList(){

        // user id reference
        userID = mAuth.getCurrentUser().getUid();

        // database reference that stores all the relationships
        CollectionReference wholerelationship = db.collection("users")
                .document(userID).collection("Relationship");

        // get the relationship queries
        wholerelationship.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            /**
             *
             * @param task
             */
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                // Lists that have all the present ingredients, required ingredients,
                // required recipe ingredients and required ingredient list

                List<List<String>> presentIngredientls = new ArrayList<>();
                List<List<String>> requiredAllIngredientls = new ArrayList<>();

                List<List<String>> requiredRecipeIngredientls = new ArrayList<>();
                List<List<String>> requiredIndividualIngredientls = new ArrayList<>();

                // for each snapshot in relationship
                for (DocumentSnapshot snapshot : task.getResult()) {

                    // if it is an ingredient that exists we add to present ingredients
                    if ((String.valueOf(snapshot.getString("type")).equals("ingredient")) &
                            (String.valueOf(snapshot.getString("exist")).equals("yes"))) {

                        List<String> aPresentIngredient = new ArrayList<String>();

                        aPresentIngredient.add(snapshot.getString("description"));
                        aPresentIngredient.add(snapshot.getString("category"));
                        aPresentIngredient.add(String.valueOf(snapshot.get("unit")));
                        aPresentIngredient.add(String.valueOf(snapshot.get("amount")));

                        presentIngredientls.add(aPresentIngredient);

                    }

                    // if it is a required ingredient not part of recipe then we add it to required ingredient list
                    if ((String.valueOf(snapshot.getString("type")).equals("ingredient")) &
                            (String.valueOf(snapshot.getString("multiple")).equals("no"))) {

                        List<String> aRequiredIngredient = new ArrayList<String>();

                        aRequiredIngredient.add(snapshot.getString("description"));
                        aRequiredIngredient.add(snapshot.getString("category"));
                        aRequiredIngredient.add(String.valueOf(snapshot.get("unit")));
                        aRequiredIngredient.add(String.valueOf(snapshot.get("amount")));

                        requiredAllIngredientls.add(aRequiredIngredient);
                        requiredIndividualIngredientls.add(aRequiredIngredient);

                    }

                    // if it is a required ingredient part of recipe then we add it to required ingredient of recipe list
                    if ((String.valueOf(snapshot.getString("type")).equals("ingredientrecipe")) &
                            (String.valueOf(snapshot.getString("multiple")).equals("yes"))) {

                        List<String> aRequiredIngredientRecipe = new ArrayList<String>();

                        aRequiredIngredientRecipe.add(snapshot.getString("description"));
                        aRequiredIngredientRecipe.add(snapshot.getString("category"));
                        aRequiredIngredientRecipe.add(String.valueOf(snapshot.get("unit")));
                        aRequiredIngredientRecipe.add(String.valueOf(snapshot.get("amount")));

                        requiredAllIngredientls.add(aRequiredIngredientRecipe);
                        requiredRecipeIngredientls.add(aRequiredIngredientRecipe);

                    }

                }

                // finally, first for each ingredinet present
                int d;

                for (d=0; d< presentIngredientls.size(); d++){


                    int h;

                    // variables that stores the total amount of the ingredient present
                    Float totalamountpresent = Float.valueOf(0);

                    totalamountpresent = Float.parseFloat(presentIngredientls.get(d).get(3));

                    // variables that stores total amount needed for ingredients required
                    Float totalunitneed = Float.valueOf(0);
                    Float totalamountneed = Float.valueOf(0);


                    // for loop over all the required ingredient list
                    for (h=0; h< requiredAllIngredientls.size(); h++){

                        // check if description and category match
                        // if so we then add it to the total amount needed for the ingredient
                        if ( (Objects.equals(presentIngredientls.get(d).get(0), requiredAllIngredientls.get(h).get(0))) &
                                (Objects.equals(presentIngredientls.get(d).get(1), requiredAllIngredientls.get(h).get(1))) ){

                            Float uniting = Float.valueOf(0);
                            uniting = Float.parseFloat(requiredAllIngredientls.get(h).get(2));

                            Float amounting = Float.valueOf(0);
                            amounting = Float.parseFloat(requiredAllIngredientls.get(h).get(3));

                            totalunitneed = totalunitneed + uniting;
                            totalamountneed = totalamountneed + amounting;

                        }

                    }

                    // if total total amount needed is more than total amount present of ingredients
                    if (((totalamountneed - totalamountpresent)>0)){

                        String showamount = "";

                        showamount = String.valueOf(totalamountneed - totalamountpresent);

                        if (totalamountneed - totalamountpresent <= 0){
                            showamount = "-";
                        }

                        // hence the difference is the needed amount and we add this to our adapter for the shopping ingredient model list
                        ShoppingListIngredientModel shoppingListIngredientModel2 = new ShoppingListIngredientModel( presentIngredientls.get(d).get(0),
                                presentIngredientls.get(d).get(1), presentIngredientls.get(d).get(2), showamount, presentIngredientls.get(d).get(0) );

                        ShoppingListIngredientModelList.add(shoppingListIngredientModel2);

                        // Final update to let Adapter know dataset changed
                        shoppingListIngredientAdapter.notifyDataSetChanged();

                    }

                }


                // earlier we checked if ingredient category and description matched is present. However, if they aren't
                // we can simply show the missing ingredients with its amount needed
                int u;

                for (u=0; u< requiredAllIngredientls.size(); u++){

                    int l;

                    // check condition
                    String met="";

                    // this for loop simply removes all the ingredients that we checked for earlier which has some
                    // amount of ingredient inside shopping list
                    for (l=0; l< presentIngredientls.size(); l++){

                        if ( (Objects.equals(presentIngredientls.get(l).get(0), requiredAllIngredientls.get(u).get(0))) &
                                (Objects.equals(presentIngredientls.get(l).get(1), requiredAllIngredientls.get(u).get(1))) ){
                            met = "true";
                        }
                    }

                    // if check condition not met then we know ingredient does not exist
                    // we now show this missing ingredient with its data
                    if (!(met.equals("true"))){

                        ShoppingListIngredientModel shoppingListIngredientModel2 = new ShoppingListIngredientModel(
                                requiredAllIngredientls.get(u).get(0), requiredAllIngredientls.get(u).get(1),
                                requiredAllIngredientls.get(u).get(2), requiredAllIngredientls.get(u).get(3),
                                requiredAllIngredientls.get(u).get(0) );

                        ShoppingListIngredientModelList.add(shoppingListIngredientModel2);

                        // Final update to let Adapter know dataset changed
                        shoppingListIngredientAdapter.notifyDataSetChanged();

                    }

                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }



}
