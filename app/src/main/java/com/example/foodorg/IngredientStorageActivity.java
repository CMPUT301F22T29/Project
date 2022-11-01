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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class IngredientStorageActivity extends AppCompatActivity implements IngredientStorageAdapter.OnEditListner{

    private Button returnHome;
    private Button add;



    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    private IngredientStorageAdapter ingredientStorageAdapter;
    private List<IngredientStorageModel> ingredientStorageModelList;

    AlertDialog alertDialog;

    String userID;
    private FirebaseAuth mAuth;

    //String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_storage);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        returnHome = findViewById(R.id.returnButtonIngredientStorage);
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IngredientStorageActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });

        add = findViewById(R.id.AddButtonIngredientStorage);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
                Toast.makeText(IngredientStorageActivity.this, "Dialog shown", Toast.LENGTH_SHORT).show();
            }
        });



        recyclerView = findViewById(R.id.ingredientListRecyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        ingredientStorageModelList = new ArrayList<>();
        ingredientStorageAdapter = new IngredientStorageAdapter(this, ingredientStorageModelList,this::onEditClick);

        recyclerView.setAdapter(ingredientStorageAdapter);


//        CollectionReference collectionReference = db.collection("users")

        showData();


    }


    private void showCustomDialog(){

        final Dialog dialog = new Dialog(IngredientStorageActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.input_layout);

        //initializing edit text
        final EditText namek = dialog.findViewById(R.id.item_input);
        final EditText categoryk= dialog.findViewById(R.id.category_input);

        //to close the dialog
        final ImageView closeAlert = dialog.findViewById(R.id.closeAlert);

        closeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button submitButton = dialog.findViewById(R.id.btnEdit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference documentReferenceReference = db.collection("users").document(userID).collection("Ingredient_Storage").document();
                String id = documentReferenceReference.getId();
                String name = namek.getText().toString();
                String category = categoryk.getText().toString();


                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(category)){
                    Toast.makeText(IngredientStorageActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    System.out.println(ingredientStorageModelList.size());
                    IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(name, category,id);
                    ingredientStorageModelList.add(ingredientStorageModel);
                    System.out.println(ingredientStorageModelList.size());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("description", name);
                    map.put("category", category);
                    map.put("id", id);

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

    }
    //dialog.dismiss();
    //ingredientAdapter.notifyDataSetChanged();


    private void showData(){

        CollectionReference collectionReference = db.collection("users");
        collectionReference.document(userID).collection("Ingredient_Storage").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ingredientStorageModelList.clear();

                        for (DocumentSnapshot snapshot : task.getResult()){
                            IngredientStorageModel ingredientStorageModel = new IngredientStorageModel(snapshot.getString("description"), snapshot.getString("category"), snapshot.getString("id"));
                            ingredientStorageModelList.add(ingredientStorageModel);
                            //System.out.println(ingredientModelList.size());

                        }

                        //Toast.makeText(IngredientActivity.this, "Ingredients shown", Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(this).inflate(R.layout.input_layout, null);
        ImageView closeAlert = view.findViewById(R.id.closeAlert);
        //Close dialog button
        closeAlert.setOnClickListener(v -> {
            alertDialog.cancel();
        });
        //Initializing
        Button btnEdit = view.findViewById(R.id.btnEdit);
        EditText na = view.findViewById(R.id.item_input);
        EditText cat = view.findViewById(R.id.category_input);
        String findID = ingredientStorageModelList.get(curPosition).getDocumentID();
        na.setText(listData.getDescription());
        cat.setText(listData.getCategory());
        CollectionReference collectionReference = db.collection("users");




        builderObj.setCancelable(false);
        builderObj.setView(view);

        btnEdit.setOnClickListener(v -> {



            String name = na.getText().toString();
            String count = cat.getText().toString();


            //Error checking for missing fields on edit
            if (na.length() == 0) {
                na.setError("Enter name");
            }else if (cat.length() == 0) {
                cat.setError("Enter name");

            } else {
                //editing the values and information
                editContact(name,count,findID, curPosition);
                HashMap<String,Object> data = new HashMap<>();
                data.put("description",name);
                data.put("category",count);
                data.put("id",findID);
                collectionReference.document(userID).collection("Ingredient_Storage").document(findID)
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
    public void editContact(String name, String count,String docID,int currentPosition){
        IngredientStorageModel obj = new IngredientStorageModel(name,count,docID);
        obj.setDescription(name);
        obj.setCategory(count);
        ingredientStorageAdapter.editDatalist(obj,currentPosition);
        alertDialog.cancel();


    }

}