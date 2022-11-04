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

/**
 *  IngredientofRecipeAdapter is the adapter class for the ingredients inside
 *  IngredientofRecipeActivity recyclerview. It contains the constructor for this adapter class.
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class IngredientOfRecipeAdapter extends RecyclerView.Adapter<IngredientOfRecipeAdapter.MyViewHolder>  {

    private Context context;
    private List<IngredientOfRecipeModel> ingredientModelList;
    private OnEditListner TheOnEditListener;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;


    /**
     * This is constructor for the adapter
     * @param context context
     * @param ingredientModelList list of the ingredient models
     * @param onEditListener editListener to edit
     */
    public IngredientOfRecipeAdapter(Context context, List<IngredientOfRecipeModel> ingredientModelList, OnEditListner onEditListener) {
        this.context = context;
        this.ingredientModelList = ingredientModelList;
        this.TheOnEditListener = onEditListener;
    }

    /**
     * Create the view
     * @param parent parent
     * @param viewType type of view integer
     * @return MyViewHolder(v) which is a view
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item_of_recipe, parent, false);
        return new MyViewHolder(v);
    }

    /**
     * This method assigns the values to the view variables
     * which were initialized in MyViewHolder
     *
     * @param holder which is the holder for the view
     * @param position which is position of view clicked
     */
    @Override
    public void onBindViewHolder(@NonNull IngredientOfRecipeAdapter.MyViewHolder holder, int position) {

        // set The Strings description, location, amount, unit, category for the Model class respectively
        holder.description.setText(ingredientModelList.get(position).getDescription());
        holder.category.setText(ingredientModelList.get(position).getCategory());
        holder.unit.setText(ingredientModelList.get(position).getUnit());
        holder.amount.setText(ingredientModelList.get(position).getAmount());

        // editButton listener to edit the ingredient
        holder.editbutt.setOnClickListener(new View.OnClickListener() {
            /**
             * edit the ingredient of the recipe
             * @param v view
             */
            @Override
            public void onClick(View v) {
                TheOnEditListener.onEditClick(ingredientModelList.get(position),position);
            }
        });

        // deleteButton listener
        holder.delbutt.setOnClickListener(new View.OnClickListener() {
            /**
             * Delete the ingredient
             * @param view which is the view
             */
            @Override
            public void onClick(View view) {

                // First initialize the id for the ingredient in the ingredients list
                // to match it with our FireStore database. In addition
                // initialize user Authentication instances, also userID and recipeID
                String findID = ingredientModelList.get(position).getDocumentID();
                String recipeID = ingredientModelList.get(position).getRecipeID();
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();

                // Firestore removes the ingredient and sends error accordingly
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

    /**
     * getItemCount method
     * @return the size of the ingredient of recipe list
     */
    @Override
    public int getItemCount() {
        return ingredientModelList.size();
    }

    /**
     * The MyViewHolder classes extends RecyclerView.ViewHolder to
     * initialize the variables for it based on the id's
     * in the layout
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // Create variables for the respective buttons and the textViews on the View
        TextView description, category,unit,amount;
        Button editbutt,delbutt;

        /**
         * find the items in the view
         * @param itemView which is the view
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.name_ingredient);
            category = itemView.findViewById(R.id.category_ingredient);
            amount= itemView.findViewById(R.id.amount_ingredient);
            unit = itemView.findViewById(R.id.unit_ingredient);
            editbutt = itemView.findViewById(R.id.editIngredientBtn);
            delbutt = itemView.findViewById(R.id.deleteIngredientBtn);

        }
    }

    /**
     *  OnEditListner is the interface which needs to be implemented
     *  so that the EditClick method is used to edit the views
     */
    public interface OnEditListner {
        void onEditClick(IngredientOfRecipeModel listData, int curPosition);
    }

    /**
     * editDatalist method edits and sets the new data
     * @param dataClassObj which is the specific ingredient of recipe storage model
     * @param curpos which is the position of the ingredient of recipe model
     */
    public void editDatalist(IngredientOfRecipeModel dataClassObj, int curpos){
        ingredientModelList.get(curpos).setDescription(dataClassObj.getDescription());
        ingredientModelList.get(curpos).setCategory(dataClassObj.getCategory());
        ingredientModelList.get(curpos).setDocumentID(dataClassObj.getDocumentID());
        ingredientModelList.get(curpos).setRecipeID(dataClassObj.getRecipeID());
        ingredientModelList.get(curpos).setAmount(dataClassObj.getAmount());
        ingredientModelList.get(curpos).setUnit(dataClassObj.getUnit());

        notifyDataSetChanged();
    }
}
