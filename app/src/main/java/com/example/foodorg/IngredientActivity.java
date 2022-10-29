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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


import java.util.ArrayList;
import java.util.HashMap;

public class IngredientActivity extends AppCompatActivity implements IngredientAdapter.OnEditListener {
    Button add;
    Button returnButt;
    ArrayList<Ingredient> FoodModelArrayList;
    RecyclerView Food;
    IngredientAdapter food;
    String location;
    AlertDialog alertDialog;
    FirebaseFirestore fStore;
    String userID;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient);
        Food = findViewById(R.id.ingredientListRecyclerView);


        FoodModelArrayList = new ArrayList<Ingredient>();
        food = new IngredientAdapter(FoodModelArrayList,IngredientActivity.this,this);
        Food.setAdapter(food);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        add = findViewById(R.id.AddButtonIngredientStorage);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // the add button opens dialog to enter information
                showCustomDialog();
            }
        });

        returnButt = findViewById(R.id.returnButtonIngredientStorage);
        returnButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(IngredientActivity.this,HomePageActivity.class);
                startActivity(i);
            }
        });

        CollectionReference collectionReference = fStore.collection("Users");


        collectionReference.document(userID).collection("Ingredients").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                FoodModelArrayList.clear();
                if (error != null){
                    Log.e("Firestore error",error.getMessage());
                    return;
                }

                for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {

                    //String name = doc.getId();
                    String name = (String) doc.getData().get("Title");
                    String count = (String) doc.getData().get("Count");
                    FoodModelArrayList.add(new Ingredient(name, count)); // Adding the cities and provinces from FireStore
                }

                food.notifyDataSetChanged();

            }


        });


    }


    //dialog to add
    private void showCustomDialog() {
        //what to display on dialog
        final Dialog dialog = new Dialog(IngredientActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.input_layout);


        //initializing edit text
        final EditText namek = dialog.findViewById(R.id.item_input);
        final EditText countk= dialog.findViewById(R.id.count_input);



        //to close the dialog
        final ImageView closeAlert = dialog.findViewById(R.id.closeAlert);

        closeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        CollectionReference collectionReference = fStore.collection("Users");
        //Location spinner

        //submit button functionalities
        Button submitButton = dialog.findViewById(R.id.btnEdit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();


                String name = namek.getText().toString();
                String count = countk.getText().toString();


                //check for empty fields error
                if (namek.length() == 0) {
                    namek.setError("Enter name");
                }else if(countk.length()==0){
                    countk.setError("Enter count");
                } else {
                    addFood(name,count);
                    dialog.dismiss();

                    HashMap<String,String> data = new HashMap<>();
                    data.put("Title",name);
                    data.put("Count",count);
                    collectionReference.document(userID).collection("Ingredients")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, name + " has been added successfully!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, name + " could not be added!");
                                }
                            });


                }
            }

        });
        dialog.show();


        //userID = mAuth.getCurrentUser().getUid();



    }





    public void addFood(String name, String count) {

        Ingredient obj = new Ingredient(name,count);
        obj.setDescription(name);
        obj.setCount(count);

        FoodModelArrayList.add(obj);
        Food = findViewById(R.id.ingredientListRecyclerView);
        food = new IngredientAdapter(FoodModelArrayList, this, this::onEditClick);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        Food.setLayoutManager(llm);
        Food.setAdapter(food);

    }

    @Override

    public void onEditClick(Ingredient listData, int curPosition) {
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
        EditText co = view.findViewById(R.id.count_input);
        na.setText(listData.getDescription());
        co.setText(listData.getCount());


        builderObj.setCancelable(false);
        builderObj.setView(view);

        btnEdit.setOnClickListener(v -> {

            String name = na.getText().toString();
            String count = co.getText().toString();

            String locate = location;
            //Error checking for missing fields on edit
            if (na.length() == 0) {
                na.setError("Enter name");
            }else if (co.length() == 0) {
                co.setError("Enter name");

            } else {
                //editing the values and information
                editContact(name,count, curPosition);
            }
        });

        alertDialog = builderObj.create();
        alertDialog.show();
    }


    //Editing to recycler view
    public void editContact(String name, String count,int currentPosition){
        Ingredient obj = new Ingredient(name,count);
        obj.setDescription(name);
        obj.setCount(count);
        food.editDatalist(obj,currentPosition);
        alertDialog.cancel();


    }

}