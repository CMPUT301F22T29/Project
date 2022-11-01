package com.example.foodorg;



import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IngredientActivity extends AppCompatActivity implements IngredientAdapter.OnEditListner{

    private Button returnHome;
    private Button add;
    //private Button delete;


    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    private IngredientAdapter ingredientAdapter;
    private List<IngredientModel> ingredientModelList;

    AlertDialog alertDialog;

    String userID;
    private FirebaseAuth mAuth;

    //String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        returnHome = findViewById(R.id.returnButtonIngredientStorage);
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IngredientActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });

        add = findViewById(R.id.AddButtonIngredientStorage);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
                Toast.makeText(IngredientActivity.this, "Dialog shown", Toast.LENGTH_SHORT).show();
            }
        });



        recyclerView = findViewById(R.id.ingredientListRecyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        ingredientModelList = new ArrayList<>();
        ingredientAdapter = new IngredientAdapter(this, ingredientModelList,this::onEditClick);

        recyclerView.setAdapter(ingredientAdapter);


//        CollectionReference collectionReference = db.collection("users")

        showData();


    }


    private void showCustomDialog(){

        final Dialog dialog = new Dialog(IngredientActivity.this);

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
                DocumentReference documentReferenceReference = db.collection("users").document(userID).collection("ingredient").document();
                String id = documentReferenceReference.getId();
                String name = namek.getText().toString();
                String category = categoryk.getText().toString();


                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(category)){
                    Toast.makeText(IngredientActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    System.out.println(ingredientModelList.size());
                    IngredientModel ingredientModel = new IngredientModel(name, category,id);
                    ingredientModelList.add(ingredientModel);
                    System.out.println(ingredientModelList.size());

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
        //dialog.dismiss();
        //ingredientAdapter.notifyDataSetChanged();
        dialog.show();

    }
    //dialog.dismiss();
    //ingredientAdapter.notifyDataSetChanged();


    private void showData(){

        CollectionReference collectionReference = db.collection("users");
        collectionReference.document(userID).collection("ingredient").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ingredientModelList.clear();

                        for (DocumentSnapshot snapshot : task.getResult()){
                            IngredientModel ingredientModel = new IngredientModel(snapshot.getString("description"), snapshot.getString("category"), snapshot.getString("id"));
                            ingredientModelList.add(ingredientModel);
                            //System.out.println(ingredientModelList.size());

                        }

                        //Toast.makeText(IngredientActivity.this, "Ingredients shown", Toast.LENGTH_SHORT).show();
                        ingredientAdapter.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(IngredientActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onEditClick(IngredientModel listData, int curPosition) {

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
        String findID = ingredientModelList.get(curPosition).getDocumentID();
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
                collectionReference.document(userID).collection("ingredient").document(findID)
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
                                Toast.makeText(IngredientActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });


            }

        });

        alertDialog = builderObj.create();
        alertDialog.show();
    }


    //Editing to recycler view
    public void editContact(String name, String count,String docID,int currentPosition){
        IngredientModel obj = new IngredientModel(name,count,docID);
        obj.setDescription(name);
        obj.setCategory(count);
        ingredientAdapter.editDatalist(obj,currentPosition);
        alertDialog.cancel();


    }

}