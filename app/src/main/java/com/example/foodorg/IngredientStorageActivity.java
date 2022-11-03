package com.example.foodorg;

import static android.content.ContentValues.TAG;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class IngredientStorageActivity extends AppCompatActivity implements IngredientStorageAdapter.OnEditListner{

    private Button returnHomeFromIStorage;
    private Button addIStorageItem;

    Spinner IStorageSpinner;

    private RecyclerView IStorageItemsRecyclerView;
    private FirebaseFirestore Firestoredb;

    private IngredientStorageAdapter ingredientStorageAdapter;
    private List<IngredientStorageModel> ingredientStorageModelList;

    private AlertDialog alertDialog;

    private String userID;
    private FirebaseAuth FireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_storage);

        FireAuth = FirebaseAuth.getInstance();
        userID = FireAuth.getCurrentUser().getUid();

        returnHomeFromIStorage = findViewById(R.id.returnButtonIngredientStorage);
        returnHomeFromIStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IngredientStorageActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });

        addIStorageItem = findViewById(R.id.AddButtonIngredientStorage);
        addIStorageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
                Toast.makeText(IngredientStorageActivity.this, "Dialog shown", Toast.LENGTH_SHORT).show();
            }
        });

        IStorageItemsRecyclerView = findViewById(R.id.ingredientListRecyclerView);

        IStorageItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Firestoredb = FirebaseFirestore.getInstance();

        FireAuth = FirebaseAuth.getInstance();

        ingredientStorageModelList = new ArrayList<>();
        ingredientStorageAdapter = new IngredientStorageAdapter(this, ingredientStorageModelList,this::onEditClick);

        IStorageItemsRecyclerView.setAdapter(ingredientStorageAdapter);


        IStorageSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.IStorageSortSpinnerList, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        IStorageSpinner.setAdapter(adapter);

        IStorageSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                        System.out.println(IStorageSpinner.getItemAtPosition(position));
                        System.out.println("nice");

                        if (IStorageSpinner.getItemAtPosition(position) == "bestBefore"){
                            Toast.makeText(IngredientStorageActivity.this, "nice", Toast.LENGTH_SHORT).show();
                        }
                        orderData(String.valueOf(IStorageSpinner.getItemAtPosition(position)));
                        //Toast.makeText(IngredientStorageActivity.this, "what you clicked " +
                                //IStorageSpinner.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );


        showData();


    }


    private void showCustomDialog(){

        final Dialog dialog = new Dialog(IngredientStorageActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.input_ingredient_storage);


        //initializing edit text
        final EditText nameEditText = dialog.findViewById(R.id.nameInputAddIStorageItem);
        final EditText descriptionEditText = dialog.findViewById(R.id.inputDescriptionIStorage);
        final EditText categoryEditText= dialog.findViewById(R.id.categoryInputAddIStorageItem);

        //final  EditText bestBeforeEditText = dialog.findViewById(R.id.inputBestBeforeAddIStorage);

        final  EditText locationEditText = dialog.findViewById(R.id.inputLocationAddIStorage);
        final  EditText amountEditText = dialog.findViewById(R.id.inputAmountAddIStorage);
        final  EditText unitEditText = dialog.findViewById(R.id.inputUnitAddIStorage);

        final DatePicker datePickerAddIStorageItem = dialog.findViewById(R.id.datePickerAddIStorageItem);

        //to close the dialog
        final ImageView closeAlert = dialog.findViewById(R.id.closeAlertInputAddIngredientStorage);

        closeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button submitButton = dialog.findViewById(R.id.btnAddIStorageItem);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FireAuth = FirebaseAuth.getInstance();
                Firestoredb = FirebaseFirestore.getInstance();
                userID = FireAuth.getCurrentUser().getUid();
                DocumentReference documentReferenceReference = Firestoredb.collection("users").document(userID).collection("Ingredient_Storage").document();

                String idIS = documentReferenceReference.getId();

                String nameIS = nameEditText.getText().toString();
                String descriptionIS = descriptionEditText.getText().toString();
                String categoryIS = categoryEditText.getText().toString();

                //String bbIS = bestBeforeEditText.getText().toString();

                String locationIS = locationEditText.getText().toString();
                String amountIS = amountEditText.getText().toString();
                String unitIS = unitEditText.getText().toString();

                int day = datePickerAddIStorageItem.getDayOfMonth();
                int month = datePickerAddIStorageItem.getMonth();
                int year = datePickerAddIStorageItem.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String formatedDate = sdf.format(calendar.getTime());

                String bbIS = formatedDate;


                if(TextUtils.isEmpty(nameIS) || TextUtils.isEmpty(descriptionIS) || TextUtils.isEmpty(categoryIS) ||
                TextUtils.isEmpty(bbIS) || TextUtils.isEmpty(locationIS) || TextUtils.isEmpty(amountIS) ||
                TextUtils.isEmpty(unitIS)){
                    Toast.makeText(IngredientStorageActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    System.out.println(ingredientStorageModelList.size());
                    IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(nameIS, descriptionIS, bbIS, locationIS, amountIS, unitIS, categoryIS, idIS);
                    ingredientStorageModelList.add(ingredientStorageModel);
                    System.out.println(ingredientStorageModelList.size());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", nameIS);
                    map.put("description", descriptionIS);

                    map.put("bestBefore", bbIS);
                    map.put("location", locationIS);
                    map.put("amount", amountIS);
                    map.put("unit", unitIS);

                    map.put("category", categoryIS);
                    map.put("id", idIS);

                    documentReferenceReference.set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
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
        //dialog.dismiss();
        //ingredientAdapter.notifyDataSetChanged();
        dialog.show();

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
    //dialog.dismiss();
    //ingredientAdapter.notifyDataSetChanged();


    private void showData(){

        CollectionReference collectionReference = Firestoredb.collection("users");
        collectionReference.document(userID).collection("Ingredient_Storage")
                .orderBy("bestBefore")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ingredientStorageModelList.clear();

                        for (DocumentSnapshot snapshot : task.getResult()){
                            IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(snapshot.getString("name"), snapshot.getString("description"),
                                    snapshot.getString("bestBefore"), snapshot.getString("location"), snapshot.getString("amount"),
                                    snapshot.getString("unit"), snapshot.getString("category"), snapshot.getString("id"));
                            ingredientStorageModelList.add(ingredientStorageModel);

                        }

                        ingredientStorageAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(IngredientActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void orderData(String by){

        CollectionReference collectionReference = Firestoredb.collection("users");
        collectionReference.document(userID).collection("Ingredient_Storage")
                .orderBy(by)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ingredientStorageModelList.clear();

                        for (DocumentSnapshot snapshot : task.getResult()){
                            IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(snapshot.getString("name"), snapshot.getString("description"),
                                    snapshot.getString("bestBefore"), snapshot.getString("location"), snapshot.getString("amount"),
                                    snapshot.getString("unit"), snapshot.getString("category"), snapshot.getString("id"));
                            ingredientStorageModelList.add(ingredientStorageModel);

                        }

                        ingredientStorageAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(IngredientActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }



    @Override
    public void onEditClick(IngredientStorageModel listData, int curPosition) {

        //Show dialog
        AlertDialog.Builder builderObj = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.edit_input_ingredient_storage, null);

        ImageView closeAlert = view.findViewById(R.id.closeAlertInputEditIngredientStorage);
        //Close dialog button
        closeAlert.setOnClickListener(v -> {
            alertDialog.cancel();
        });


        //Initializing
        Button btnEditIS = view.findViewById(R.id.btnEditIStorageItem);

        EditText EditNameIS = view.findViewById(R.id.nameInputEditIStorageItem);
        EditText EditDescriptionIS = view.findViewById(R.id.inputDescriptionEditIStorage);
        EditText EditCategoryIS = view.findViewById(R.id.categoryInputEditIStorageItem);

        //EditText EditBestBeforeIS = view.findViewById(R.id.inputBestBeforeEditIStorage);

        EditText EditLocationIS = view.findViewById(R.id.inputLocationEditIStorage);
        EditText EditAmountIS = view.findViewById(R.id.inputAmountEditIStorage);
        EditText EditUnitIS = view.findViewById(R.id.inputUnitEditIStorage);

        Toast.makeText(IngredientStorageActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();

        DatePicker datePickerEditIStorageItem = view.findViewById(R.id.datePickerEditIStorageItem);

        String ingredientID = ingredientStorageModelList.get(curPosition).getDocumentID();

        EditNameIS.setText(listData.getName());
        EditDescriptionIS.setText(listData.getDescription());
        EditCategoryIS.setText(listData.getCategory());

        //EditBestBeforeIS.setText(listData.getBestBefore());

        EditLocationIS.setText(listData.getLocation());
        EditAmountIS.setText(listData.getAmount());
        EditUnitIS.setText(listData.getUnit());



        String thedate = listData.getBestBefore();
        String[] datemonthyear = thedate.split("-", 3);

        //System.out.println(Integer.parseInt(datemonthyear[1]));
        //System.out.println(Integer.parseInt(datemonthyear[1])-1);

        //System.out.println(Integer.parseInt(datemonthyear[0]));
        //System.out.println("nice");
        //System.out.println(Integer.parseInt(datemonthyear[2]));

        datePickerEditIStorageItem.updateDate(Integer.parseInt(datemonthyear[2]),Integer.parseInt(datemonthyear[1])-1,Integer.parseInt(datemonthyear[0]));


        CollectionReference collectionReference = Firestoredb.collection("users");

        builderObj.setCancelable(false);
        builderObj.setView(view);

        btnEditIS.setOnClickListener(v -> {

            String nameIngredient = EditNameIS.getText().toString();
            String descriptionIngredient = EditDescriptionIS.getText().toString();
            String categoryIngredient = EditCategoryIS.getText().toString();

            //String bestBeforeIngredient = EditBestBeforeIS.getText().toString();
            String locationIngredient = EditLocationIS.getText().toString();
            String amountIngredient = EditAmountIS.getText().toString();
            String unitIngredient = EditUnitIS.getText().toString();


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
                return;
            } else {
                //editing the values and information
                editContact(nameIngredient, descriptionIngredient, bestBeforeIngredient, locationIngredient
                        , amountIngredient, unitIngredient, categoryIngredient, ingredientID, curPosition);
                HashMap<String,Object> data = new HashMap<>();

                data.put("name",nameIngredient);
                data.put("description",descriptionIngredient);

                data.put("bestBefore", bestBeforeIngredient);
                data.put("location", locationIngredient);
                data.put("amount", amountIngredient);
                data.put("unit", unitIngredient);

                data.put("category",categoryIngredient);
                data.put("id",ingredientID);

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

        alertDialog = builderObj.create();
        alertDialog.show();

    }


    //Editing to recycler view
    public void editContact(String name, String description, String bestBefore, String location, String amount, String unit, String count,String docID,int currentPosition){
        IngredientStorageModel obj = new IngredientStorageModel(name, description, bestBefore, location, amount, unit, count,docID);
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