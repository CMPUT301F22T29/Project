package com.example.foodorg;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * RecipeActivity is the activity the user access to store recipes in the Recipes Storage
 * or access the recipes. The activity contains
 * <ul>
 *     <li>Add button to add recipes</li>
 *     <li>Return to HomePage Activity Button to return home</li>
 *     <li>Sort spinner to sort the Recipes</li>
 *     <li>Recyclerview for the recipes</li>
 * </ul>
 * @author amman1
 * @author mohaimin
 */
public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.OnEditListner {

    private Button returnHome;
    private Button add;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private RecipeAdapter recipeAdapter;
    private RecipeMealPlanAdapter recipeMealPlanAdapter;
    private List<RecipeModel> recipeModelList;
    private AlertDialog alertDialog;
    private Spinner recipeSpinner;
    private String userID;
    private FirebaseAuth mAuth;
    private String validity;

    /**
     *
     * @param savedInstanceState bundle of arguments
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipepage);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        returnHome = findViewById(R.id.returnButtonRecipe);
        String validity= getIntent().getStringExtra("key");

        if (validity.equals("0")){
            add = findViewById(R.id.AddButtonRecipe);
            add.setVisibility(View.INVISIBLE);
            recipeSpinner = (Spinner) findViewById(R.id.spinnerRecipe);
            recipeSpinner.setVisibility(View.INVISIBLE);
        }
        else{
            add = findViewById(R.id.AddButtonRecipe);
        }




        // OnClickListener to return home
        returnHome.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v view
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecipeActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });


        add = findViewById(R.id.AddButtonRecipe);
        // OnClickListener to add ingredient
        add.setOnClickListener(new View.OnClickListener() {
            /**
             * This onClick shows the custom dialog which adds the recipes
             * @param v view
             */
            @Override
            public void onClick(View v) {
                showCustomDialog();
                Toast.makeText(RecipeActivity.this, "Dialog shown", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize recyclerview
        recyclerView = findViewById(R.id.RecipeListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database and authentication
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize the list for the recipes, and the adapter



        if (validity.equals("0")){
            recipeModelList = new ArrayList<>();
            recipeMealPlanAdapter = new RecipeMealPlanAdapter(RecipeActivity.this, recipeModelList);
            recyclerView.setAdapter(recipeMealPlanAdapter);
        }
        else{
            recipeModelList = new ArrayList<>();
            recipeAdapter = new RecipeAdapter(RecipeActivity.this, recipeModelList, this::onEditClick);
            recyclerView.setAdapter(recipeAdapter);
        }


        // Initialize spinner for sorting recipes
        recipeSpinner = (Spinner) findViewById(R.id.spinnerRecipe);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.recipeSpinnerList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        recipeSpinner.setAdapter(adapter);

        // OnItemSelectedListener for sorting by each item (string value of sort)
        recipeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    /**
                     *
                     * @param parent parent spinner
                     * @param view string view in spinner
                     * @param position position of the item to be ordered by string
                     * @param id id of the item to be ordered by string
                     */
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        orderDataRecipe(String.valueOf(recipeSpinner.getItemAtPosition(position)),validity);
                        Toast.makeText(RecipeActivity.this, "Sorted by " +
                        recipeSpinner.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );


        showData(validity);
    }

    /**
     * orderData shows the recyclerview ordered by String by
     * @param orderBy which is the string value for ordering the data
     */
    private void orderDataRecipe(String orderBy,String valid) {

        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = db.collection("users");
        collectionReference.document(userID).collection("Recipes")
                .orderBy(orderBy)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * onComplete method for the task
                     * @param task which is the task for firestore
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // First clear recipes list, then add recipe based on the models as stored in Firestore
                        // which will be in order as given by the String orderBy
                        recipeModelList.clear();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            RecipeModel recipeModel = new RecipeModel(snapshot.getString("title"), snapshot.getString("category"),
                                    String.valueOf(snapshot.getLong("time").intValue()),String.valueOf(snapshot.getLong("servings").intValue()), snapshot.getString("comments"),
                                    snapshot.getString("id"));
                            recipeModelList.add(recipeModel);
                        }
                        if (valid.equals("0")){
                            recipeMealPlanAdapter.notifyDataSetChanged();
                        }
                        else{
                            recipeAdapter.notifyDataSetChanged();
                        }
                        //recipeAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecipeActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    /**
     * showCustomDialog() shows the dialog for adding a new Recipe
     */
    private void showCustomDialog(){

        // Initialize the dialog and its settings
        final Dialog dialog = new Dialog(RecipeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.recipe_input);

        //initializing edit text
        final EditText titleRecipe = dialog.findViewById(R.id.title_recipe_input);
        final EditText categoryRecipe= dialog.findViewById(R.id.category_recipe_input);
        final EditText timeRecipe = dialog.findViewById(R.id.time_recipe_input);
        final EditText servingsRecipe= dialog.findViewById(R.id.servings_recipe_input);
        final EditText commentsRecipe = dialog.findViewById(R.id.comment_recipe_input);

        //to close the dialog
        final ImageView closeAlert = dialog.findViewById(R.id.closeAlert);
        closeAlert.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view view of closeAlert
             */
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // submitButton OnClickListener to add details to a recipe
        Button submitButton = dialog.findViewById(R.id.btnRecipeEdit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This onClick finally adds the new details to Firestore if correct details
             * entered, and updates the recyclerview; otherwise sends appropriate toast message
             * @param v which is the submitButton view
             */
            @Override
            public void onClick(View v) {

                // Initialize the firebase Authentication, the database,
                // the userid and the document reference for the ingredient storage
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference documentReferenceReference = db.collection("users").document(userID).collection("Recipes").document();

                String id = documentReferenceReference.getId();
                String title = titleRecipe.getText().toString();
                String category = categoryRecipe.getText().toString();
                String time = timeRecipe.getText().toString();
                String servings = servingsRecipe.getText().toString();
                String comments = commentsRecipe.getText().toString();

                // Check if fields are empty
                if(TextUtils.isEmpty(title) || TextUtils.isEmpty(category) || TextUtils.isEmpty(time) || TextUtils.isEmpty(servings) || TextUtils.isEmpty(comments)){
                    Toast.makeText(RecipeActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();

                }else {

                    // Otherwise we can finally add the recipe to our recipeModelList
                    RecipeModel recipeModel = new RecipeModel(title, category,time,servings,comments,id);
                    recipeModelList.add(recipeModel);

                    Integer time_number = Integer.parseInt(time);
                    Integer servings_number = Integer.parseInt(servings);

                    // Next we create a hashmap where we set the data into the firestore
                    // based on the hashmap
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("title", title);
                    map.put("category", category);
                    map.put("time", time_number);
                    map.put("servings", servings_number);
                    map.put("comments", comments);
                    map.put("id", id);
                    documentReferenceReference.set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                /**
                                 *
                                 * @param aVoid which references void
                                 */
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    recipeAdapter.notifyDataSetChanged();
                                    Intent i = new Intent(RecipeActivity.this, IngredientOfRecipeActivity.class);
                                    i.putExtra("recipe_id",id);
                                    startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                }

            }
        });

        dialog.show();

        // The below are code to set dialog to 80% of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        int dialogWindowHeight = (int) (displayHeight * 0.8f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        dialog.getWindow().setAttributes(layoutParams);

    }


    /**
     * showData shows the recyclerview
     */
    private void showData(String valid){

        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = db.collection("users");
        collectionReference.document(userID).collection("Recipes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * onComplete method for the task
                     * @param task which is the task for firestore
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // clear the list and then add data from firestore
                        recipeModelList.clear();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            RecipeModel recipeModel = new RecipeModel(snapshot.getString("title"), snapshot.getString("category"),
                                    String.valueOf(snapshot.getLong("time").intValue()),String.valueOf(snapshot.getLong("servings").intValue()), snapshot.getString("comments"),
                                    snapshot.getString("id"));
                            recipeModelList.add(recipeModel);
                        }
                        if (valid.equals("0")){
                            recipeMealPlanAdapter.notifyDataSetChanged();
                        }
                        else{
                            recipeAdapter.notifyDataSetChanged();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecipeActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    /**
     * Edit the data of the recipe
     * @param title title
     * @param category category
     * @param time time
     * @param servings servings
     * @param comments comments
     * @param docID docID
     * @param currentPosition currentPosition
     */
    public void editContact(String title, String category, String time, String servings, String comments, String docID,int currentPosition){
        RecipeModel obj = new RecipeModel(title,category,time,servings,comments,docID);
        obj.setTitle(title);
        obj.setCategory(category);
        obj.setTime(time);
        obj.setServings(servings);
        obj.setComments(comments);
        obj.setDocumentID(docID);
        recipeAdapter.editDatalist(obj,currentPosition);
        alertDialog.cancel();

    }


    /**
     * onEditClick is the method that edits the RecipeModel data based on the
     * position value given by {Integer curPosition}
     * @param listData which is the specific data model
     * @param curPosition which is position of the model
     */
    @Override
    public void onEditClick(RecipeModel listData, int curPosition) {

        // Intialize Dialog
        AlertDialog.Builder builderObj = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.recipe_input, null);
        ImageView closeAlert = view.findViewById(R.id.closeAlert);

        //Close dialog button
        closeAlert.setOnClickListener(v -> {
            alertDialog.cancel();
        });

        // Initialize the button for editing/ the editTexts
        Button btnEdit = view.findViewById(R.id.btnRecipeEdit);
        EditText nameEditRecipe = view.findViewById(R.id.title_recipe_input);
        EditText categoryEditRecipe = view.findViewById(R.id.category_recipe_input);
        EditText preparationTimeEditRecipe = view.findViewById(R.id.time_recipe_input);
        EditText servingsEditRecipe = view.findViewById(R.id.servings_recipe_input);
        EditText commentsEditRecipe = view.findViewById(R.id.comment_recipe_input);

        String findID = recipeModelList.get(curPosition).getDocumentID();
        nameEditRecipe.setText(listData.getTitle());
        categoryEditRecipe.setText(listData.getCategory());
        preparationTimeEditRecipe.setText(listData.getTime());
        servingsEditRecipe.setText(listData.getServings());
        commentsEditRecipe.setText(listData.getComments());
        CollectionReference collectionReference = db.collection("users");

        // Dialog Setting
        builderObj.setCancelable(false);
        builderObj.setView(view);

        // OnClickListener for editing the recipe
        btnEdit.setOnClickListener(v -> {

            // the have the appropriate String values for each variables
            // taken from their respective EditTexts
            String title = nameEditRecipe.getText().toString();
            String category = categoryEditRecipe.getText().toString();
            String time = preparationTimeEditRecipe.getText().toString();
            String servings = servingsEditRecipe.getText().toString();
            String comments = commentsEditRecipe.getText().toString();


            //Error checking for missing fields on editTexts
            if (nameEditRecipe.length() == 0) {
                nameEditRecipe.setError("Enter name");
            }else if (categoryEditRecipe.length() == 0) {
                categoryEditRecipe.setError("Enter category");
            }else if (preparationTimeEditRecipe.length() == 0) {
                preparationTimeEditRecipe.setError("Enter prep time");
            }else if (servingsEditRecipe.length() == 0) {
                servingsEditRecipe.setError("Enter servings");
            }else if (commentsEditRecipe.length() == 0) {
                commentsEditRecipe.setError("Enter name");
            } else {

                //editing the values and information
                editContact(title,category,time,servings,comments,findID, curPosition);

                // Next we create a hashmap where we set the data into the firestore
                // based on the hashmap
                HashMap<String,Object> data = new HashMap<>();
                Integer time_number = Integer.parseInt(time);
                Integer servings_number = Integer.parseInt(servings);
                data.put("title", title);
                data.put("category", category);
                data.put("time", time_number);
                data.put("servings", servings_number);
                data.put("comments", comments);
                data.put("id",findID);
                collectionReference.document(userID).collection("Recipes").document(findID)
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            /**
                             *
                             * @param aVoid void reference
                             */
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot updated successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                                Toast.makeText(RecipeActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });

        alertDialog = builderObj.create();
        alertDialog.show();

    }


}