package com.example.foodorg;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
public class IngredientStorageAdapter extends RecyclerView.Adapter<IngredientStorageAdapter.MyViewHolder>  {

    // Initialize the Context, the ingredient storage list for the ingredient storage models,
    // the onEditListener method for editing the adapter, the Firestore database,
    // the userID and the FireBase Authentication
    private Context context;
    private static List<IngredientStorageModel> ingredientStorageModelList;
    private OnEditListner TheOnEditListener;
    private FirebaseFirestore db;
    private String userID;
    private FirebaseAuth mAuth;


    /**
     *
     * @param context context for the adapter
     * @param ingredientStorageModelList list for the ingredient models
     * @param onEditListener method which listens to edits on adapter
     */
    public IngredientStorageAdapter(Context context, List<IngredientStorageModel> ingredientStorageModelList, OnEditListner onEditListener) {
        // assign each variable
        this.context = context;
        this.ingredientStorageModelList = ingredientStorageModelList;
        this.TheOnEditListener = onEditListener;
    }

    /**
     * This method creates the ViewHolder
     *
     * @param parent which is parent
     * @param viewType which is viewType
     * @return MyViewHolder(v) which is a view
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_storage_item, parent, false);
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        // set The Strings for name, description, best before date,
        // location, amount, unit, category and documentID for
        // the Model class respectively
        holder.storageItemName.setText(ingredientStorageModelList.get(position).getName());
        holder.storageItemDescription.setText(ingredientStorageModelList.get(position).getDescription());
        holder.storageItemCategory.setText(ingredientStorageModelList.get(position).getCategory());
        holder.storageItemBB.setText(ingredientStorageModelList.get(position).getBestBefore());
        holder.storageItemLocation.setText(ingredientStorageModelList.get(position).getLocation());
        holder.storageItemAmount.setText(ingredientStorageModelList.get(position).getAmount());
        holder.storageItemUnit.setText(ingredientStorageModelList.get(position).getUnit());

        boolean isExpanded = ingredientStorageModelList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);


        // onClickListener for the edit button
        holder.editBtnIStorage.setOnClickListener(new View.OnClickListener() {
            /**
             * this will initialize the method to edit the view
             * @param v which is the view
             */
            @Override
            public void onClick(View v) {
                TheOnEditListener.onEditClick(ingredientStorageModelList.get(position), position);
            }
        });

        // onClickListener for the delete button
        holder.delBtnIStorage.setOnClickListener(new View.OnClickListener() {
            /**
             * this onClick will delete the view, both from the
             * adapter and from FireStore
             * @param view which is the view
             */
            @Override
            public void onClick(View view) {

                // First initialize the id for the ingredient in the ingredients list
                // to match it with our FireStore database. In addition
                // initialize user Authentication instances
                String findID = ingredientStorageModelList.get(position).getDocumentID();
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();

                String descrition = ingredientStorageModelList.get(position).getDescription();

                // Also initialize the current user id
                userID = mAuth.getCurrentUser().getUid();

                // Firestore removes the ingredient and sends error accordingly
                DocumentReference collectionReference = db.collection("users").document(userID).collection("Ingredient_Storage").document(findID.toString());
                collectionReference
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ingredientStorageModelList.remove(position);
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

                CollectionReference relationship = db.collection("users")
                        .document(userID).collection("Relationship");

                relationship
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            /**
                             * onComplete method for the task
                             * @param task which is the task for firestore
                             */
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                for (DocumentSnapshot snapshot : task.getResult()){
                                    if ((String.valueOf(snapshot.getString("id")).equals(findID.toString())) &
                                            (String.valueOf(snapshot.getString("exist")).equals("yes"))){
                                        relationship.document(snapshot.getId()).delete();
                                    }
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
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
     * @return the size of the ingredient storage list
     */
    @Override
    public int getItemCount() {
        return ingredientStorageModelList.size();
    }

    /**
     * The MyViewHolder classes extends RecyclerView.ViewHolder to
     * initialize the variables for it based on the id's
     * in the layout
     */
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView storageItemName, storageItemDescription, storageItemCategory;
        TextView storageItemBB, storageItemLocation, storageItemAmount, storageItemUnit;
        Button editBtnIStorage,delBtnIStorage;

        LinearLayout expandableLayout;

        /**
         * find the items in the view
         * @param itemView which is the view
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            storageItemName = itemView.findViewById(R.id.storageItemViewName);
            storageItemCategory = itemView.findViewById(R.id.storageItemViewCategory);
            storageItemDescription = itemView.findViewById(R.id.storageItemViewDescription);
            storageItemBB = itemView.findViewById(R.id.storageItemViewBestBefore);
            storageItemLocation = itemView.findViewById(R.id.storageItemViewLocation);
            storageItemAmount = itemView.findViewById(R.id.storageItemViewAmount);
            storageItemUnit = itemView.findViewById(R.id.storageItemViewUnit);
            editBtnIStorage = itemView.findViewById(R.id.editIngredient);
            delBtnIStorage = itemView.findViewById(R.id.deleteIngredient);

            expandableLayout = itemView.findViewById(R.id.ingredientStorageExpandable);

            storageItemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IngredientStorageModel ingredientStorageModel = ingredientStorageModelList.get(getAdapterPosition());
                    ingredientStorageModel.setExpanded(!ingredientStorageModel.isExpanded());
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
        void onEditClick(IngredientStorageModel listData, int curPosition);

    }

    /**
     * editDatalist method edits and sets the new data
     * @param dataClassObj which is the specific ingredient storage model
     * @param curpos which is the position of the ingredient model
     */
    public void editDatalist(IngredientStorageModel dataClassObj, int curpos){

        ingredientStorageModelList.get(curpos).setName(dataClassObj.getName());
        ingredientStorageModelList.get(curpos).setDescription(dataClassObj.getDescription());
        ingredientStorageModelList.get(curpos).setCategory(dataClassObj.getCategory());
        ingredientStorageModelList.get(curpos).setDocumentID(dataClassObj.getDocumentID());

        ingredientStorageModelList.get(curpos).setBestBefore(dataClassObj.getBestBefore());
        ingredientStorageModelList.get(curpos).setLocation(dataClassObj.getLocation());
        ingredientStorageModelList.get(curpos).setAmount(dataClassObj.getAmount());
        ingredientStorageModelList.get(curpos).setUnit(dataClassObj.getUnit());

        notifyDataSetChanged();
    }
}
