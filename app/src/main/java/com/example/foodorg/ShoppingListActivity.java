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
 *     <li>Add button</li>
 *     <li>Return to HomePage Activity Button</li>
 *     <li>Sort spinner</li>
 *     <li>Recyclerview for the ingredients</li>
 * </ul>
 * @author amman1
 * @author mohaimin
 */
public class ShoppingListActivity extends AppCompatActivity {

    private Button returnHome;
    private Button addShoppingListItem;

    private RecyclerView ShoppingListItemsRecyclerView;;
    private ShoppingListIngredientAdapter shoppingListIngredientAdapter;
    private List<ShoppingListIngredientModel> ShoppingListIngredientModelList;

    private AlertDialog alertDialog;

    private FirebaseFirestore Firestoredb;
    private MealPlanAdapter mealPlanAdapter;
    private List<MealPlanModel> mealPlanModelList;
    private FirebaseAuth FireAuth;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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



        mealPlanModelList = new ArrayList<>();
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



    boolean subset(List<String> list, List<String> sublist) {
        return Collections.indexOfSubList(list, sublist) != -1;
    }

    List<String> intersection(List<String> list1, List<String> list2){

        HashSet<String> set = new HashSet<>();

        set.addAll(list1);
        set.retainAll(list2);

        List<String> arr = new ArrayList<>(set);

        return arr;


    }


    private void showShoppingList(){

        userID = mAuth.getCurrentUser().getUid();

        CollectionReference wholerelationship = db.collection("users")
                .document(userID).collection("Relationship");

        wholerelationship.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                List<List<String>> presentIngredientls = new ArrayList<>();
                List<List<String>> requiredAllIngredientls = new ArrayList<>();

                List<List<String>> requiredRecipeIngredientls = new ArrayList<>();
                List<List<String>> requiredIndividualIngredientls = new ArrayList<>();

                for (DocumentSnapshot snapshot : task.getResult()) {

                    if ((String.valueOf(snapshot.getString("type")).equals("ingredient")) &
                            (String.valueOf(snapshot.getString("exist")).equals("yes"))) {

                        List<String> aPresentIngredient = new ArrayList<String>();

                        aPresentIngredient.add(snapshot.getString("description"));
                        aPresentIngredient.add(snapshot.getString("category"));
                        aPresentIngredient.add(String.valueOf(snapshot.get("unit")));
                        aPresentIngredient.add(String.valueOf(snapshot.get("amount")));

                        presentIngredientls.add(aPresentIngredient);

                    }

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

                int d;

                for (d=0; d< presentIngredientls.size(); d++){


                    int h;

                    Float totalamountpresent = Float.valueOf(0);

                    totalamountpresent = Float.parseFloat(presentIngredientls.get(d).get(3));


                    Float totalunitneed = Float.valueOf(0);
                    Float totalamountneed = Float.valueOf(0);


                    for (h=0; h< requiredAllIngredientls.size(); h++){

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

                    if (((totalamountneed - totalamountpresent)>0)){

                        String showamount = "";

                        showamount = String.valueOf(totalamountneed - totalamountpresent);

                        if (totalamountneed - totalamountpresent <= 0){
                            showamount = "-";
                        }


                        ShoppingListIngredientModel shoppingListIngredientModel2 = new ShoppingListIngredientModel( presentIngredientls.get(d).get(0),
                                presentIngredientls.get(d).get(1), presentIngredientls.get(d).get(2), showamount, presentIngredientls.get(d).get(0) );

                        ShoppingListIngredientModelList.add(shoppingListIngredientModel2);

                        // Final update to let Adapter know dataset changed
                        shoppingListIngredientAdapter.notifyDataSetChanged();

                    }

                }

                int u;

                for (u=0; u< requiredAllIngredientls.size(); u++){

                    int l;

                    String met="";

                    for (l=0; l< presentIngredientls.size(); l++){

                        if ( (Objects.equals(presentIngredientls.get(l).get(0), requiredAllIngredientls.get(u).get(0))) &
                                (Objects.equals(presentIngredientls.get(l).get(1), requiredAllIngredientls.get(u).get(1))) ){
                            met = "true";
                        }
                    }

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
