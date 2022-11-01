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

public class RecipeActivity extends AppCompatActivity implements RecipeAdapter.OnEditListner {

    Button returnHome;
    private Button add;

    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    private RecipeAdapter recipeAdapter;
    private List<RecipeModel> recipeModelList;

    AlertDialog alertDialog;

    String userID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipepage);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        returnHome = findViewById(R.id.returnButtonRecipe);

        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RecipeActivity.this, HomePageActivity.class);
                startActivity(i);
            }
        });


        add = findViewById(R.id.AddButtonRecipe);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
                Toast.makeText(RecipeActivity.this, "Dialog shown", Toast.LENGTH_SHORT).show();
            }
        });


        recyclerView = findViewById(R.id.RecipeListRecyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        recipeModelList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(RecipeActivity.this, recipeModelList, this::onEditClick);

        recyclerView.setAdapter(recipeAdapter);

//        CollectionReference collectionReference = db.collection("users")

        showData();
    }



    private void showCustomDialog(){

        final Dialog dialog = new Dialog(RecipeActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.recipe_input);

        //initializing edit text
        final EditText titlek = dialog.findViewById(R.id.title_recipe_input);
        final EditText categoryk= dialog.findViewById(R.id.category_recipe_input);
        final EditText timek = dialog.findViewById(R.id.time_recipe_input);
        final EditText servingsk= dialog.findViewById(R.id.servings_recipe_input);
        final EditText commentsk = dialog.findViewById(R.id.comment_recipe_input);


        //to close the dialog
        final ImageView closeAlert = dialog.findViewById(R.id.closeAlert);

        closeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button submitButton = dialog.findViewById(R.id.btnRecipeEdit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference documentReferenceReference = db.collection("users").document(userID).collection("Recipes").document();
                String id = documentReferenceReference.getId();
                String title = titlek.getText().toString();
                String category = categoryk.getText().toString();
                String time = timek.getText().toString();
                String servings = servingsk.getText().toString();
                String comments = commentsk.getText().toString();



                if(TextUtils.isEmpty(title) || TextUtils.isEmpty(category) || TextUtils.isEmpty(time) || TextUtils.isEmpty(servings) || TextUtils.isEmpty(comments)){
                    Toast.makeText(RecipeActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    System.out.println(recipeModelList.size());
                    RecipeModel recipeModel = new RecipeModel(title, category,time,servings,comments,id);
                    recipeModelList.add(recipeModel);
                    System.out.println(recipeModelList.size());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("title", title);
                    map.put("category", category);
                    map.put("time", time);
                    map.put("servings", servings);
                    map.put("comments", comments);
                    map.put("id", id);

                    documentReferenceReference.set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    IngredientsListCustomDialog(id);
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    recipeAdapter.notifyDataSetChanged();
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

    private void IngredientsListCustomDialog(String findID) {



        final Dialog dialog = new Dialog(RecipeActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_ingredients_recipe);

        final ImageView closeAlert = dialog.findViewById(R.id.closeAlert);

        closeAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button ingredientAddBtn = dialog.findViewById(R.id.addIngredientsRecipeBtn);
        ingredientAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecipeActivity.this, IngredientActivity.class);
                i.putExtra("recipe_id",findID);
                startActivity(i);
                dialog.dismiss();

            }



        });

        dialog.show();


    }


    private void showData(){

        CollectionReference collectionReference = db.collection("users");
        collectionReference.document(userID).collection("Recipes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        recipeModelList.clear();

                        for (DocumentSnapshot snapshot : task.getResult()){
                            RecipeModel recipeModel = new RecipeModel(snapshot.getString("title"), snapshot.getString("category"), snapshot.getString("time"),snapshot.getString("servings"), snapshot.getString("comments"), snapshot.getString("id"));
                            recipeModelList.add(recipeModel);
                            //System.out.println(ingredientModelList.size());

                        }

                        //Toast.makeText(IngredientActivity.this, "Ingredients shown", Toast.LENGTH_SHORT).show();
                        recipeAdapter.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(IngredientActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }



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


    @Override
    public void onEditClick(RecipeModel listData, int curPosition) {
        AlertDialog.Builder builderObj = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.recipe_input, null);
        ImageView closeAlert = view.findViewById(R.id.closeAlert);
        //Close dialog button
        closeAlert.setOnClickListener(v -> {
            alertDialog.cancel();
        });
        //Initializing
        Button btnEdit = view.findViewById(R.id.btnRecipeEdit);
        EditText na = view.findViewById(R.id.title_recipe_input);
        EditText cat = view.findViewById(R.id.category_recipe_input);
        EditText prep = view.findViewById(R.id.time_recipe_input);
        EditText serv = view.findViewById(R.id.servings_recipe_input);
        EditText comm = view.findViewById(R.id.comment_recipe_input);

        String findID = recipeModelList.get(curPosition).getDocumentID();
        na.setText(listData.getTitle());
        cat.setText(listData.getCategory());
        prep.setText(listData.getTime());
        serv.setText(listData.getServings());
        comm.setText(listData.getComments());
        CollectionReference collectionReference = db.collection("users");




        builderObj.setCancelable(false);
        builderObj.setView(view);

        btnEdit.setOnClickListener(v -> {



            String title = na.getText().toString();
            String category = cat.getText().toString();
            String time = prep.getText().toString();
            String servings = serv.getText().toString();
            String comments = comm.getText().toString();


            //Error checking for missing fields on edit
            if (na.length() == 0) {
                na.setError("Enter name");
            }else if (cat.length() == 0) {
                cat.setError("Enter category");
            }else if (prep.length() == 0) {
                prep.setError("Enter prep time");
            }else if (serv.length() == 0) {
                serv.setError("Enter servings");
            }else if (comm.length() == 0) {
                comm.setError("Enter name");
            } else {
                //editing the values and information
                editContact(title,category,time,servings,comments,findID, curPosition);
                HashMap<String,Object> data = new HashMap<>();
                data.put("title", title);
                data.put("category", category);
                data.put("time", time);
                data.put("servings", servings);
                data.put("comments", comments);
                data.put("id",findID);
                collectionReference.document(userID).collection("Recipes").document(findID)
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
                                Toast.makeText(RecipeActivity.this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                IngredientsListCustomDialog(findID);



            }

        });

        alertDialog = builderObj.create();
        alertDialog.show();

    }


}