package com.example.foodorg;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * IngredientOfStorageActivity is the activity the user access to store ingredients in the ingredient storage
 * or access the ingredients. The activity contains
 * <ul>
 *     <li>Add ingredient to the recipe Button</li>
 *     <li>Return to Recipe Activity Button</li>
 *     <li>Recyclerview for the ingredients</li>
 * </ul>
 * @author amman1
 * @author mohaimin
 */
public class IngredientOfRecipeActivity extends AppCompatActivity implements IngredientOfRecipeAdapter.OnEditListner {
    private Button returnHome;
    private Button add;
    private RecipeAdapter recipeAdapter;
    private List<RecipeModel> recipeModelList;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private IngredientOfRecipeAdapter ingredientAdapter;
    private List<IngredientOfRecipeModel> ingredientModelList;
    private AlertDialog alertDialog;
    private String recipeID;
    private String userID;
    private FirebaseAuth mAuth;
    private ImageView recipeImage;

    /**
     *
     * @param savedInstanceState bundle of arguments
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_of_recipe_activity);

        // Initialize firebase authentication and get the current user
        // and recipeID of the ingredient
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        recipeID= getIntent().getExtras().getString("recipe_id");

        // Initialize the returnHomeButton
        returnHome = findViewById(R.id.returnButtonIngredientStorage);
        returnHome.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v view
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IngredientOfRecipeActivity.this, RecipeActivity.class);
                startActivity(i);
            }
        });

        // Initialize the add ingredient Button
        add = findViewById(R.id.AddButtonIngredientStorage);
        add.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v view
             */
            @Override
            public void onClick(View v) {
                showCustomDialog();
                Toast.makeText(IngredientOfRecipeActivity.this, "Dialog shown", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize recyclerview
        recyclerView = findViewById(R.id.ingredientOnlyListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the instance of the firestore database
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // initialize the model list of the ingredients, as well as the adapter for it and set it
        ingredientModelList = new ArrayList<>();
        ingredientAdapter = new IngredientOfRecipeAdapter(this, ingredientModelList,this::onEditClick);

        recyclerView.setAdapter(ingredientAdapter);

        // show the recipe details at the tope
        showRecipeDetails();
        showData();
        showImage();


    }

    private void showImage() {
        recipeID= getIntent().getExtras().getString("recipe_id");
        recipeImage = (ImageView)findViewById(R.id.ingredientRecipeImageView);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("recipe_images/"+recipeID);
        try{
            final File localFile = File.createTempFile(recipeID,"jpeg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            recipeImage.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(IngredientOfRecipeActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showRecipeDetails(){

        // Initialize the respective textViews to show the recipe details
        // This allows user to know he is adding ingredients for this specific recipe
        TextView recipeTitle = (TextView)findViewById(R.id.recipe_tile);
        TextView recipePrepTime = (TextView)findViewById(R.id.prep_time_recipe);
        TextView recipeServings = (TextView) findViewById(R.id.servings_recipe);
        TextView recipeComments = (TextView) findViewById(R.id.comments_recipe);
        TextView recipeCategory = (TextView)findViewById(R.id.category_recipe);
        recipeModelList = new ArrayList<>();

        // Initialize the firebase Authentication, the database,
        // the userid and the document reference for the ingredient storage
        DocumentReference collectionReference = db.collection("users").document(userID);
        collectionReference.collection("Recipes").document(recipeID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    /**
                     *
                     * @param task task to be done
                     */
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // First clear ingredient list, then add ingredients based on the models as stored in Firestore
                        recipeModelList.clear();
                        if(task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            RecipeModel recipeModel = new RecipeModel(doc.getString("title"), doc.getString("category"),
                                    String.valueOf(doc.getLong("time").intValue()),String.valueOf(doc.getLong("servings").intValue()),
                                    doc.getString("comments"), doc.getString("id"));
                            recipeModelList.add(recipeModel);

                            // finally set the recipe details in the activity
                            recipeTitle.setText("Ingredients list for "+recipeModel.getTitle());
                            recipePrepTime.setText(recipeModel.getTime());
                            recipeServings.setText(recipeModel.getServings());
                            recipeComments.setText(recipeModel.getComments());
                            recipeCategory.setText(recipeModel.getCategory());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(IngredientOfRecipeActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });



    }


    /**
     * showCustomDialog() shows the dialog for adding a new Ingredient
     */
    private void showCustomDialog(){


        // initialize the dialog, its settings and layout
        final Dialog dialog = new Dialog(IngredientOfRecipeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.input_layout);

        // initializing edit texts and the recipe id
        recipeID= getIntent().getExtras().getString("recipe_id");
        final EditText nameText = dialog.findViewById(R.id.item_input);
        final EditText categoryText= dialog.findViewById(R.id.category_input);
        final EditText amountText = dialog.findViewById(R.id.amount_input);
        final EditText costText = dialog.findViewById(R.id.unit_input);

        //to close the dialog
        final ImageView closeAlert = dialog.findViewById(R.id.closeAlert);
        closeAlert.setOnClickListener(new View.OnClickListener() {
            /**
             * @param view which is closeAlert
             */
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Finally the submit button to add the ingredient
        Button submitButton = dialog.findViewById(R.id.btnEdit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize the firebase Authentication, the database,
                // the userid and the document reference for the ingredient storage
                // specifically for recipe
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference documentReferenceReference = db.collection("users").document(userID)
                        .collection("Recipes").document(recipeID).collection("Ingredients").document();

                // next the have the appropriate String values for each variables
                // taken from their respective EditTexts
                String id = documentReferenceReference.getId();
                String name = nameText.getText().toString();
                String category = categoryText.getText().toString();
                String unit = costText.getText().toString();
                String amount = amountText.getText().toString();

                // Check if Strings are empty, else continue
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(category) || TextUtils.isEmpty(unit) || TextUtils.isEmpty(amount)){
                    Toast.makeText(IngredientOfRecipeActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    // Finally add the ingredient for the recipe
                    recipeID= getIntent().getExtras().getString("recipe_id");
                    IngredientOfRecipeModel ingredientModel = new IngredientOfRecipeModel(name, category,id,recipeID,amount,unit);
                    ingredientModelList.add(ingredientModel);

                    // Next we create a hashmap where we set the data into the firestore
                    // based on the hashmap
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("description", name);
                    map.put("category", category);
                    map.put("id", id);
                    map.put("recipe_id",recipeID);
                    map.put("unit",unit);
                    map.put("amount",amount);
                    documentReferenceReference.set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                /**
                                 *
                                 * @param aVoid void reference
                                 */
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    ingredientAdapter.notifyDataSetChanged();
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
     * howData puts all the ingredients into the model list, and then shows the data
     */
    private void showData(){

        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = db.collection("users");
        collectionReference.document(userID).collection("Recipes").document(recipeID).collection("Ingredients").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     *
                     * @param task the task
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // First clear ingredient list, then add ingredients based on the models as stored in Firestore
                        ingredientModelList.clear();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            IngredientOfRecipeModel ingredientModel = new IngredientOfRecipeModel(snapshot.getString("description"), snapshot.getString("category"), snapshot.getString("id"),snapshot.getString("recipe_id"),snapshot.getString("unit"),snapshot.getString("amount"));
                            ingredientModelList.add(ingredientModel);
                        }
                        ingredientAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(IngredientOfRecipeActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    //Editing to recycler view
    public void editContact(String name, String count,String docID,String recipeID,String unit,String amount,int currentPosition){

        IngredientOfRecipeModel obj = new IngredientOfRecipeModel(name,count,docID,recipeID,unit,amount);
        obj.setDescription(name);
        obj.setCategory(count);
        //obj.setRecipeID(recipeID);
        ingredientAdapter.editDatalist(obj,currentPosition);
        alertDialog.cancel();


    }


    /**
     * onEditClick is the method that edits the IngredientModel data based on the
     * position value given by curPosition
     * @param listData which is the specific data model
     * @param curPosition which is position of the model
     */
    @Override
    public void onEditClick(IngredientOfRecipeModel listData, int curPosition) {

        AlertDialog.Builder builderObj = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.input_layout, null);
        ImageView closeAlert = view.findViewById(R.id.closeAlert);
        //Close dialog button
        closeAlert.setOnClickListener(v -> {
            alertDialog.cancel();
        });
        //Initializing the Buttons and the EditTexts
        Button btnEdit = view.findViewById(R.id.btnEdit);
        EditText nameInput = view.findViewById(R.id.item_input);
        EditText categoryInput = view.findViewById(R.id.category_input);
        EditText amountInput = view.findViewById(R.id.amount_input);
        EditText unitInput = view.findViewById(R.id.unit_input);
        String findID = ingredientModelList.get(curPosition).getDocumentID();
        String findrecipeID = ingredientModelList.get(curPosition).getRecipeID();

        // Setting the texts to the EditTexts from previous
        nameInput.setText(listData.getDescription());
        categoryInput.setText(listData.getCategory());
        amountInput.setText(listData.getAmount());
        unitInput.setText(listData.getUnit());

        // Creating the collection reference by user
        CollectionReference collectionReference = db.collection("users");

        // AlertDialog Setting
        builderObj.setCancelable(false);
        builderObj.setView(view);

        // OnClickListener to add the ingredient to a recipe
        btnEdit.setOnClickListener(v -> {

            // String variables for the Button
            String name = nameInput.getText().toString();
            String category = categoryInput.getText().toString();
            String amount = amountInput.getText().toString();
            String unit = unitInput.getText().toString();

            //Error checking for missing fields on edit
            if (nameInput.length() == 0) {
                nameInput.setError("Enter name");
            }else if (categoryInput.length() == 0) {
                categoryInput.setError("Enter name");
            }else if (amountInput.length() == 0) {
                amountInput.setError("Enter name");
            }else if (unitInput.length() == 0) {
                unitInput.setError("Enter name");

            } else {
                //editing the values and information
                editContact(name,category,findID,findrecipeID,unit,amount, curPosition);
                // Next we create a hashmap where we set the data into the firestore
                // based on the hashmap
                HashMap<String,Object> data = new HashMap<>();
                data.put("description",name);
                data.put("category",category);
                data.put("unit",unit);
                data.put("amount",amount);
                data.put("id",findID);
                data.put("recipe_id",findrecipeID);
                collectionReference.document(userID).collection("Recipes").document(findrecipeID).collection("Ingredients").document(findID)
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            /**
                             *
                             * @param aVoid a void reference
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
                                Toast.makeText(IngredientOfRecipeActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });
        alertDialog = builderObj.create();
        alertDialog.show();

    }
}





