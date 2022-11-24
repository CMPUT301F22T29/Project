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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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


        // Initialize the add ingredient Button
        addShoppingListItem = findViewById(R.id.AddButtonShoppingList);
        addShoppingListItem.setOnClickListener(new View.OnClickListener() {
            /**
             * This onClick creates the dialog that first adds the ingredient
             * @param v view
             */
            @Override
            public void onClick(View v) {
                //showCustomDialog();
                Toast.makeText(ShoppingListActivity.this, "Dialog shown", Toast.LENGTH_SHORT).show();
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
        showData();

    }


    /**
     * showCustomDialog() shows the dialog for adding a new Ingredient
     */
    private void showCustomDialog() {

        // initialize the dialog, its settings and layout
        final Dialog dialog = new Dialog(ShoppingListActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.shoppinglistiteminput);


        // initializing edit text for the name, description,
        // amount, unit, category for the Model class respectively
        final EditText nameEditText = dialog.findViewById(R.id.nameInputShoppingList);
        final EditText descriptionEditText = dialog.findViewById(R.id.inputDescriptionShoppingList);
        final EditText categoryEditText= dialog.findViewById(R.id.inputCategoryShoppingList);
        final  EditText amountEditText = dialog.findViewById(R.id.inputAmountShoppingList);
        final  EditText unitEditText = dialog.findViewById(R.id.inputUnitShoppingList);


        // Initialize ImageView to to close the dialog
        final ImageView closeAlert = dialog.findViewById(R.id.closeAlertInputShoppingList);

        // listener to close dialog
        closeAlert.setOnClickListener(new View.OnClickListener() {
            /**
             * close dialog
             * @param view which is the closeAlert
             */
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        Button submitButton = findViewById(R.id.btnAddShoppingList);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void showData() {

        ShoppingListIngredientModelList.clear();
        ShoppingListIngredientModel shoppingListIngredientModel = new ShoppingListIngredientModel("indian orange", "fruit", "2", "3", "orange");
        ShoppingListIngredientModelList.add(shoppingListIngredientModel);

        ShoppingListIngredientModel shoppingListIngredientModel1 = new ShoppingListIngredientModel("indian orange", "fruit", "2", "3", "orange");
        ShoppingListIngredientModelList.add(shoppingListIngredientModel1);

        // Final update to let Adapter know dataset changed
        shoppingListIngredientAdapter.notifyDataSetChanged();

    }
}