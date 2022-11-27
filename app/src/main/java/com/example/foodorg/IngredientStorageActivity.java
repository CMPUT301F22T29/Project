package com.example.foodorg;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import org.checkerframework.checker.units.qual.C;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * IngredientStorageActivity is the activity the user access to store ingredients in the ingredient storage
 * or access the ingredients. The activity contains
 * <ul>
 *     <li>Add button</li>
 *     <li>Return to HomePage Activity Button</li>
 *     <li>Sort spinner</li>
 *     <li>Recyclerview for the ingredients</li>
 * </ul>
 * @author amman1
 * @author mohaimin
 */
public class IngredientStorageActivity extends AppCompatActivity implements IngredientStorageAdapter.OnEditListner{

    // Create variables for the buttons for adding or going back to homepage,
    // spinner, recyclerview, firebaseFirestore,
    // IngredientStorageAdapter, IngredientList, AlertDialog, userID
    // and the firebase Authentication
    private Button returnHomeFromIStorage;
    private Button addIStorageItem;
    private Spinner IStorageSpinner;
    private RecyclerView IStorageItemsRecyclerView;
    private FirebaseFirestore Firestoredb;
    private IngredientStorageAdapter ingredientStorageAdapter;

    private IngredientMealPlanAdapter ingredientMealPlanAdapter;

    private List<IngredientStorageModel> ingredientStorageModelList;
    private AlertDialog alertDialog;
    private String userID;
    private FirebaseAuth FireAuth;

    /**
     *
     * @param savedInstanceState bundle of arguments
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.ingredient_storage);



        String validity= getIntent().getStringExtra("key");


        if (validity.equals("2")){
            setContentView(R.layout.ingredient_storage_mealplan);
        }
        else{
            setContentView(R.layout.ingredient_storage);
            addIStorageItem = findViewById(R.id.AddButtonIngredientStorage);
            addIStorageItem.setOnClickListener(new View.OnClickListener() {
                /**
                 * This onClick creates the dialog that first adds the ingredient
                 * @param v view
                 */
                @Override
                public void onClick(View v) {
                    showCustomDialog();
                    Toast.makeText(IngredientStorageActivity.this, "Dialog shown", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Initialize firebase authentication and get the current user
        // and userID of the user
        FireAuth = FirebaseAuth.getInstance();
        userID = FireAuth.getCurrentUser().getUid();

        // Initialize the returnHomeButton
        returnHomeFromIStorage = findViewById(R.id.returnButtonIngredientStorage);
        returnHomeFromIStorage.setOnClickListener(new View.OnClickListener() {
            /**
             * @param v view
             */
            @Override
            public void onClick(View v) {
                // Start HomePage Activity

                if (validity.equals("2")){
                    Intent i = new Intent(IngredientStorageActivity.this, MealPlanActivity.class);
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(IngredientStorageActivity.this, HomePageActivity.class);
                    startActivity(i);
                }
            }
        });

        // Initialize the add ingredient Button


        // Initialize recyclerview
        IStorageItemsRecyclerView = findViewById(R.id.ingredientListRecyclerView);
        IStorageItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize the instance of the firestore database
        Firestoredb = FirebaseFirestore.getInstance();
        FireAuth = FirebaseAuth.getInstance();

        // initialize the model list of the ingredients, as well as the adapter for it and set it

        if (validity.equals("2")){
            ingredientStorageModelList = new ArrayList<>();
            ingredientMealPlanAdapter = new IngredientMealPlanAdapter(this, ingredientStorageModelList);
            IStorageItemsRecyclerView.setAdapter(ingredientMealPlanAdapter);
        }
        else{
            ingredientStorageModelList = new ArrayList<>();
            ingredientStorageAdapter = new IngredientStorageAdapter(this, ingredientStorageModelList,this::onEditClick);
            IStorageItemsRecyclerView.setAdapter(ingredientStorageAdapter);

            IStorageSpinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.IStorageSortSpinnerList, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Apply the adapter to the spinner
            IStorageSpinner.setAdapter(adapter);

            // Spinner onItemSelected listener for what is clicked
            IStorageSpinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        /**
                         *
                         * @param parent parent
                         * @param view view
                         * @param position position of view
                         * @param id id of view
                         */
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            if (String.valueOf(IStorageSpinner.getItemAtPosition(position)).equals("bestBefore")){
                                orderDateData(validity);
                            }
                            else{
                                // First we call orderData to order the Data
                                orderData(String.valueOf(IStorageSpinner.getItemAtPosition(position)),validity);
                                // Appropriate text message
                                Toast.makeText(IngredientStorageActivity.this, "Sorted by " +
                                        IStorageSpinner.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    }
            );
        }
        /*ingredientStorageModelList = new ArrayList<>();
        ingredientStorageAdapter = new IngredientStorageAdapter(this, ingredientStorageModelList,this::onEditClick);
        IStorageItemsRecyclerView.setAdapter(ingredientStorageAdapter);*/

        // Also initialize the spinner for sorting the ingredients


        // showData finally puts all the ingredients into the model list, and then shows the data
        showData(validity);


    }


    /**
     * showCustomDialog() shows the dialog for adding a new Ingredient
     */
    private void showCustomDialog(){

        // initialize the dialog, its settings and layout
        final Dialog dialog = new Dialog(IngredientStorageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.input_ingredient_storage);


        // initializing edit text for the name, description, best before date,
        // location, amount, unit, category for the Model class respectively
        final EditText nameEditText = dialog.findViewById(R.id.nameInputAddIStorageItem);
        final EditText descriptionEditText = dialog.findViewById(R.id.inputDescriptionIStorage);
        final EditText categoryEditText= dialog.findViewById(R.id.categoryInputAddIStorageItem);
        final  EditText locationEditText = dialog.findViewById(R.id.inputLocationAddIStorage);
        final  EditText amountEditText = dialog.findViewById(R.id.inputAmountAddIStorage);
        final  EditText unitEditText = dialog.findViewById(R.id.inputUnitAddIStorage);

        // Also initialize the datePicker for the stored item
        final DatePicker datePickerAddIStorageItem = dialog.findViewById(R.id.datePickerAddIStorageItem);

        // Initialize ImageView to to close the dialog
        final ImageView closeAlert = dialog.findViewById(R.id.closeAlertInputAddIngredientStorage);

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

        // Initialize the button to submit details
        Button submitButton = dialog.findViewById(R.id.btnAddIStorageItem);
        // onClickListener for the submitButton
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
                FireAuth = FirebaseAuth.getInstance();
                Firestoredb = FirebaseFirestore.getInstance();
                userID = FireAuth.getCurrentUser().getUid();
                DocumentReference documentReferenceReference = Firestoredb.collection("users").document(userID).collection("Ingredient_Storage").document();

                // first we get the id of this document so we can add as document id to our class
                String idIS = documentReferenceReference.getId();

                // next the have the appropriate String values for each variables
                // taken from their respective EditTexts
                String nameIS = nameEditText.getText().toString();
                String descriptionIS = descriptionEditText.getText().toString();
                String categoryIS = categoryEditText.getText().toString();
                String locationIS = locationEditText.getText().toString();
                String amountIS = amountEditText.getText().toString();
                String unitIS = unitEditText.getText().toString();

                // Also to get the datePicker values we use the following code
                // to then convert it into a store able string
                int day = datePickerAddIStorageItem.getDayOfMonth();
                int month = datePickerAddIStorageItem.getMonth();
                int year = datePickerAddIStorageItem.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                // Initialized date
                String bbIS = sdf.format(calendar.getTime());

                // Check if details are all empty
                if(TextUtils.isEmpty(nameIS) || TextUtils.isEmpty(descriptionIS) || TextUtils.isEmpty(categoryIS) ||
                        TextUtils.isEmpty(bbIS) || TextUtils.isEmpty(locationIS) || TextUtils.isEmpty(amountIS) ||
                        TextUtils.isEmpty(unitIS)){
                    Toast.makeText(IngredientStorageActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();

                }else {

                    // If all checks out we can finally add the Ingredient to our Ingredient Storage List
                    IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(nameIS, descriptionIS, bbIS, locationIS, amountIS, unitIS, categoryIS, idIS);
                    ingredientStorageModelList.add(ingredientStorageModel);

                    // Next we create a hashmap where we set the data into the firestore
                    // based on the hashmap
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", nameIS);
                    map.put("description", descriptionIS);
                    map.put("bestBefore", bbIS);
                    map.put("location", locationIS);
                    map.put("amount", Integer.parseInt(amountIS));
                    map.put("unit", Integer.parseInt(unitIS));
                    map.put("category", categoryIS);
                    map.put("id", idIS);
                    documentReferenceReference.set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                /**
                                 * onSuccess will send appropriate Log Message
                                 * @param aVoid an uninstantiable placeholder class to hold a reference
                                 *              to the Class object representing the Java keyword void
                                 */
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    ingredientStorageAdapter.notifyDataSetChanged();
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

        // Finally we show dialog
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
    private void showData(String valid){

        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = Firestoredb.collection("users");
        collectionReference.document(userID).collection("Ingredient_Storage")
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
                        ingredientStorageModelList.clear();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(snapshot.getString("name"), snapshot.getString("description"),
                                    snapshot.getString("bestBefore"), snapshot.getString("location"), String.valueOf(snapshot.getLong("amount").intValue()),
                                    String.valueOf(snapshot.getLong("unit").intValue()), snapshot.getString("category"), snapshot.getString("id"));
                            ingredientStorageModelList.add(ingredientStorageModel);
                        }
                        // Final update to let Adapter know dataset changed
                        if (valid.equals("2")){
                            ingredientMealPlanAdapter.notifyDataSetChanged();
                        }
                        else{
                            ingredientStorageAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(IngredientStorageActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * orderData shows the recyclerview ordered by String by
     * @param orderBy which is the string value for ordering the data
     */
    private void orderData(String orderBy, String valid){

        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = Firestoredb.collection("users");
        collectionReference.document(userID).collection("Ingredient_Storage")
                .orderBy(orderBy)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * onComplete method for the task
                     * @param task which is the task for firestore
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // First clear ingredient list, then add ingredients based on the models as stored in Firestore
                        // which will be in order as given by the String orderBy
                        ingredientStorageModelList.clear();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(snapshot.getString("name"), snapshot.getString("description"),
                                    snapshot.getString("bestBefore"), snapshot.getString("location"), String.valueOf(snapshot.getLong("amount").intValue()),
                                    String.valueOf(snapshot.getLong("unit").intValue()), snapshot.getString("category"), snapshot.getString("id"));
                            ingredientStorageModelList.add(ingredientStorageModel);
                        }
                        if (valid.equals("2")){
                            ingredientMealPlanAdapter.notifyDataSetChanged();
                        }
                        else{
                            ingredientStorageAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(IngredientStorageActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void orderDateData(String valid){

        // Access Firestore database to get the data based on userID from appropriate collectionPath
        CollectionReference collectionReference = Firestoredb.collection("users");
        collectionReference.document(userID).collection("Ingredient_Storage")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * onComplete method for the task
                     * @param task which is the task for firestore
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // First clear ingredient list, then add ingredients based on the models as stored in Firestore
                        // which will be in order as given by the String orderBy
                        ingredientStorageModelList.clear();
                        for (DocumentSnapshot snapshot : task.getResult()){
                            IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(snapshot.getString("name"), snapshot.getString("description"),
                                    snapshot.getString("bestBefore"), snapshot.getString("location"), String.valueOf(snapshot.getLong("amount").intValue()),
                                    String.valueOf(snapshot.getLong("unit").intValue()), snapshot.getString("category"), snapshot.getString("id"));
                            ingredientStorageModelList.add(ingredientStorageModel);
                        }

                        Collections.sort(ingredientStorageModelList, new Comparator<IngredientStorageModel>() {
                            @Override
                            public int compare(IngredientStorageModel ingredientStorageModel, IngredientStorageModel t1) {
                                return 0;
                            }
                        });

                        ingredientStorageModelList = sortlist(ingredientStorageModelList);
                        if (valid.equals("2")){
                            ingredientMealPlanAdapter.notifyDataSetChanged();
                        }
                        else{
                            ingredientStorageAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(IngredientStorageActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private List<IngredientStorageModel> sortlist(List<IngredientStorageModel> array){


        Collections.sort(array, new Comparator<IngredientStorageModel>() {
            @Override
            public int compare(IngredientStorageModel t1, IngredientStorageModel t2) {

                int comparison = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    comparison = sdf.parse(t1.getBestBefore()).compareTo(sdf.parse(t2.getBestBefore()));
                    return comparison;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return comparison;
            }
        });


        return array;
    }


    /**
     * onEditClick is the method that edits the IngredientModel data based on the
     * position value given by {int curPosition}
     * @param listData which is the specific data model
     * @param curPosition which is position of the model
     */
    @Override
    public void onEditClick(IngredientStorageModel listData, int curPosition) {

        //Show dialog for editing
        AlertDialog.Builder builderObj = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.edit_input_ingredient_storage, null);

        // Close the alert/ edit dialog
        ImageView closeAlert = view.findViewById(R.id.closeAlertInputEditIngredientStorage);
        closeAlert.setOnClickListener(v -> {
            alertDialog.cancel();
        });

        // initializing edit text for the name, description, best before date,
        // location, amount, unit, category for the Model class respectively
        Button btnEditIS = view.findViewById(R.id.btnEditIStorageItem);
        EditText EditNameIS = view.findViewById(R.id.nameInputEditIStorageItem);
        EditText EditDescriptionIS = view.findViewById(R.id.inputDescriptionEditIStorage);
        EditText EditCategoryIS = view.findViewById(R.id.categoryInputEditIStorageItem);
        EditText EditLocationIS = view.findViewById(R.id.inputLocationEditIStorage);
        EditText EditAmountIS = view.findViewById(R.id.inputAmountEditIStorage);
        EditText EditUnitIS = view.findViewById(R.id.inputUnitEditIStorage);

        // Also initialize the datePicker for the stored item
        DatePicker datePickerEditIStorageItem = view.findViewById(R.id.datePickerEditIStorageItem);

        // Also initialize the documentID for the stored item
        String ingredientID = ingredientStorageModelList.get(curPosition).getDocumentID();

        // First we set the values to the editTexts in the dialog based on what was previously the values
        // for the ingredient so the user doesn't have to type it all again
        EditNameIS.setText(listData.getName());
        EditDescriptionIS.setText(listData.getDescription());
        EditCategoryIS.setText(listData.getCategory());
        EditLocationIS.setText(listData.getLocation());
        EditAmountIS.setText(listData.getAmount());
        EditUnitIS.setText(listData.getUnit());

        // Code to put the spinner for the best before date as it was originally beofore
        String thedate = listData.getBestBefore();
        String[] datemonthyear = thedate.split("-", 3);
        datePickerEditIStorageItem.updateDate(Integer.parseInt(datemonthyear[2]),Integer.parseInt(datemonthyear[1])-1,Integer.parseInt(datemonthyear[0]));

        // Initialize the collectionreference based on path user
        CollectionReference collectionReference = Firestoredb.collection("users");

        // settings for the alert dialog
        builderObj.setCancelable(false);
        builderObj.setView(view);

        // OnClickListener for editing the ingredient
        btnEditIS.setOnClickListener(v -> {

            // the have the appropriate String values for each variables
            // taken from their respective EditTexts
            String nameIngredient = EditNameIS.getText().toString();
            String descriptionIngredient = EditDescriptionIS.getText().toString();
            String categoryIngredient = EditCategoryIS.getText().toString();
            String locationIngredient = EditLocationIS.getText().toString();
            String amountIngredient = EditAmountIS.getText().toString();
            String unitIngredient = EditUnitIS.getText().toString();

            // the datePicker converted into String values
            int day = datePickerEditIStorageItem.getDayOfMonth();
            int month = datePickerEditIStorageItem.getMonth();
            int year = datePickerEditIStorageItem.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formatedDate = sdf.format(calendar.getTime());
            String bestBeforeIngredient= formatedDate;

            //Error checking for missing fields on edit
            if (TextUtils.isEmpty(nameIngredient) || TextUtils.isEmpty(descriptionIngredient) || TextUtils.isEmpty(categoryIngredient)
                    || TextUtils.isEmpty(bestBeforeIngredient) || TextUtils.isEmpty(locationIngredient) || TextUtils.isEmpty(amountIngredient) ||
                    TextUtils.isEmpty(unitIngredient)) {
                Toast.makeText(IngredientStorageActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
            } else {
                //editing the values and information
                editContact(nameIngredient, descriptionIngredient, bestBeforeIngredient, locationIngredient
                        , amountIngredient, unitIngredient, categoryIngredient, ingredientID, curPosition);

                // Next we create a hashmap where we set the data into the firestore
                // based on the hashmap
                HashMap<String,Object> data = new HashMap<>();
                data.put("name",nameIngredient);
                data.put("description",descriptionIngredient);
                data.put("bestBefore", bestBeforeIngredient);
                data.put("location", locationIngredient);
                data.put("amount", Integer.valueOf(amountIngredient));
                data.put("unit", Integer.valueOf(unitIngredient));
                data.put("category",categoryIngredient);
                data.put("id",ingredientID);

                // Follow the same steps to add as in showCustomDialog
                collectionReference.document(userID).collection("Ingredient_Storage").document(ingredientID)
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot updated successfully!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                                Toast.makeText(IngredientStorageActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // show the dialog
        alertDialog = builderObj.create();
        alertDialog.show();
    }

    /**
     *
     * @param name name
     * @param description description
     * @param bestBefore bestBefore
     * @param location location
     * @param amount amount
     * @param unit unit
     * @param count count
     * @param docID docID
     * @param currentPosition currentPosition
     */
    public void editContact(String name, String description, String bestBefore, String location, String amount, String unit, String count,String docID,int currentPosition){
        IngredientStorageModel obj = new IngredientStorageModel(name, description, bestBefore, location, amount, unit, count,docID);

        // Here we set the values to the editTexts in the dialog based on what
        // was edited in the values for the ingredient
        obj.setName(name);
        obj.setDescription(description);
        obj.setBestBefore(bestBefore);
        obj.setLocation(location);
        obj.setAmount(amount);
        obj.setUnit(unit);
        obj.setCategory(count);
        ingredientStorageAdapter.editDatalist(obj,currentPosition);
        alertDialog.cancel();

    }

}