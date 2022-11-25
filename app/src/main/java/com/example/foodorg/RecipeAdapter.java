package com.example.foodorg;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
 *  IngredientStorageAdapter is the adapter class for the ingredients inside
 *  Ingredient Storage recyclerview. It contains the constructor for this adapter class
 *  which is used in the Ingredient Storage Activity
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {
    private Context context;
    private List<RecipeModel> recipeModelList;
    private OnEditListner TheOnEditListener;
    private FirebaseFirestore db;
    String userID;
    private FirebaseAuth mAuth;

    /**
     *
     * @param context context
     * @param recipeModelList list of recipes
     * @param theOnEditListener onEditListener interface
     */
    public RecipeAdapter(Context context, List<RecipeModel> recipeModelList, OnEditListner theOnEditListener) {
        this.context = context;
        this.recipeModelList = recipeModelList;
        this.TheOnEditListener = theOnEditListener;
    }

    /**
     * create the view holder
     * @param parent parent
     * @param viewType the viewType
     * @return MyViewHolder(v) which is a view
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
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
    public void onBindViewHolder(@NonNull RecipeAdapter.MyViewHolder holder, int position) {

        // set The Strings for title, category, comment, prep and serving for the Model class respectively
        holder.title.setText(recipeModelList.get(position).getTitle());
        holder.category.setText(recipeModelList.get(position).getCategory());
        holder.comment.setText(recipeModelList.get(position).getComments());
        holder.prep.setText(recipeModelList.get(position).getTime());
        holder.serving.setText(recipeModelList.get(position).getServings());

        boolean isExpanded = recipeModelList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);


        holder.cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String findID = recipeModelList.get(position).getDocumentID();
                Intent i = new Intent(context,CameraActivity.class);
                i.putExtra("recipe_id",findID);
                context.startActivity(i);
            }
        });

        // editButton listener
        holder.editbutt.setOnClickListener(new View.OnClickListener() {
            /**
             * edit the recipe
             * @param v view
             */
            @Override
            public void onClick(View v) {
                TheOnEditListener.onEditClick(recipeModelList.get(position),position);
            }
        });

        // ingredientButton listener
        holder.ingredientButton.setOnClickListener(new View.OnClickListener() {
            /**
             * go to the new activity of containing the ingredients for
             * the respective recipe
             * @param view v
             */
            @Override
            public void onClick(View view) {

                String findID = recipeModelList.get(position).getDocumentID();
                Intent i = new Intent(context, IngredientOfRecipeActivity.class);
                i.putExtra("recipe_id",findID);
                context.startActivity(i);

            }
        });

        // delete the recipe
        holder.delbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // First initialize the id for the ingredient in the ingredients list
                // to match it with our FireStore database. In addition
                // initialize user Authentication instances, also userID
                String findID = recipeModelList.get(position).getDocumentID();
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();

                // Firestore removes the recipe and sends error accordingly
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

    /**
     * getItemCount method
     * @return the size of the recipe list
     */
    @Override
    public int getItemCount() {
        return recipeModelList.size();
    }

    /**
     * The MyViewHolder classes extends RecyclerView.ViewHolder to
     * initialize the variables for it based on the id's
     * in the layout
     */
    public class MyViewHolder extends RecyclerView.ViewHolder{

        // Create variables for the respective buttons and the textViews on the View
        TextView title, category, prep, serving, comment ;
        Button editbutt,delbutt,ingredientButton,cameraBtn;
        LinearLayout expandableLayout;

        /**
         * find the items in the view
         * @param itemView which is the view
         */
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
            cameraBtn = itemView.findViewById(R.id.cameraBtn);

            expandableLayout = itemView.findViewById(R.id.recipeItemAdapter);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecipeModel recipe1 = recipeModelList.get(getAdapterPosition());
                    recipe1.setExpanded(!recipe1.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

    /**
     *  OnEditListner is the interface which needs to be implemented
     *  so that the EditClick method is used to edit the views
     */
    public interface OnEditListner {
        void onEditClick(RecipeModel listData, int curPosition);

    }

    /**
     * editDatalist method edits and sets the new data
     * @param dataClassObj which is the specific recipe model
     * @param curpos which is the position of the recipe model
     */
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

