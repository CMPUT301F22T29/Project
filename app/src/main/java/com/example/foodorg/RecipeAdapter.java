package com.example.foodorg;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {
    private Context context;
    private List<RecipeModel> recipeModelList;
    private OnEditListner TheOnEditListener;
    private FirebaseFirestore db;
    String userID;
    private FirebaseAuth mAuth;

    public RecipeAdapter(Context context, List<RecipeModel> recipeModelList, OnEditListner theOnEditListener) {
        this.context = context;
        this.recipeModelList = recipeModelList;
        this.TheOnEditListener = theOnEditListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.MyViewHolder holder, int position) {
        holder.title.setText(recipeModelList.get(position).getTitle());
        holder.category.setText(recipeModelList.get(position).getCategory());
        holder.comment.setText(recipeModelList.get(position).getComments());
        holder.prep.setText(recipeModelList.get(position).getTime());
        holder.serving.setText(recipeModelList.get(position).getServings());

        holder.editbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TheOnEditListener.onEditClick(ingredientModelList.get(position), position);
                TheOnEditListener.onEditClick(recipeModelList.get(position),position);
            }
        });

        holder.ingredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String findID = recipeModelList.get(position).getDocumentID();
                Intent i = new Intent(context,IngredientActivity.class);
                i.putExtra("recipe_id",findID);
                context.startActivity(i);

            }
        });


        holder.delbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String findID = recipeModelList.get(position).getDocumentID();
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference collectionReference = db.collection("users").document(userID).collection("Recipes").document(findID.toString());
                collectionReference
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                recipeModelList.remove(position);
                                Log.d(TAG, " has been deleted successfully!");
                                notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

            }
        });

    }



    @Override
    public int getItemCount() {
        return recipeModelList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title, category, prep, serving, comment ;
        Button editbutt,delbutt,ingredientButton;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_recipe);
            category = itemView.findViewById(R.id.categor_recipe);
            prep = itemView.findViewById(R.id.prep_recipe);
            serving = itemView.findViewById(R.id.servings);
            comment = itemView.findViewById(R.id.comment);

            editbutt = itemView.findViewById(R.id.editRecipe);
            delbutt = itemView.findViewById(R.id.deleteRecipe);
            ingredientButton = itemView.findViewById(R.id.ingredientsRecipeCardBtn);
        }
    }


    public interface OnEditListner {
        void onEditClick(RecipeModel listData, int curPosition);

    }

    public void editDatalist(RecipeModel dataClassObj, int curpos){
        recipeModelList.get(curpos).setTitle(dataClassObj.getTitle());
        recipeModelList.get(curpos).setCategory(dataClassObj.getCategory());
        recipeModelList.get(curpos).setComments(dataClassObj.getComments());
        recipeModelList.get(curpos).setServings(dataClassObj.getServings());
        recipeModelList.get(curpos).setTime(dataClassObj.getTime());
        recipeModelList.get(curpos).setDocumentID(dataClassObj.getDocumentID());

        notifyDataSetChanged();
    }
}

