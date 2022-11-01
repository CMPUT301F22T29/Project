package com.example.foodorg;

import static android.content.ContentValues.TAG;

import android.content.Context;
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

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder>  {

    private Context context;
    private List<IngredientModel> ingredientModelList;
    private OnEditListner TheOnEditListener;
    private FirebaseFirestore db;
    String userID;
    private FirebaseAuth mAuth;


    public IngredientAdapter(Context context, List<IngredientModel> ingredientModelList, OnEditListner onEditListener) {
        this.context = context;
        this.ingredientModelList = ingredientModelList;
        this.TheOnEditListener = onEditListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.MyViewHolder holder, int position) {
        holder.desc.setText(ingredientModelList.get(position).getDescription());
        holder.category.setText(ingredientModelList.get(position).getCategory());
        holder.editbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TheOnEditListener.onEditClick(ingredientModelList.get(position), position);
                TheOnEditListener.onEditClick(ingredientModelList.get(position),position);
            }
        });

        holder.delbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String findID = ingredientModelList.get(position).getDocumentID();
                String recipeID = ingredientModelList.get(position).getRecipeID();


                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();
                DocumentReference collectionReference = db.collection("users").document(userID).collection("Recipes").document(recipeID).collection("Ingredients").document(findID.toString());
                collectionReference
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ingredientModelList.remove(position);
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
        return ingredientModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView desc, category;
        Button editbutt,delbutt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            desc = itemView.findViewById(R.id.name_ingredient);
            category = itemView.findViewById(R.id.category_ingredient);
            editbutt = itemView.findViewById(R.id.editIngredientBtn);
            delbutt = itemView.findViewById(R.id.deleteIngredientBtn);

        }
    }

    public interface OnEditListner {
        void onEditClick(IngredientModel listData, int curPosition);

    }


    public void editDatalist(IngredientModel dataClassObj, int curpos){
        ingredientModelList.get(curpos).setDescription(dataClassObj.getDescription());
        ingredientModelList.get(curpos).setCategory(dataClassObj.getCategory());
        ingredientModelList.get(curpos).setDocumentID(dataClassObj.getDocumentID());
        ingredientModelList.get(curpos).setRecipeID(dataClassObj.getRecipeID());

        notifyDataSetChanged();
    }
}
