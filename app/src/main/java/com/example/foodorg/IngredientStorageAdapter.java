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

public class IngredientStorageAdapter extends RecyclerView.Adapter<IngredientStorageAdapter.MyViewHolder>  {

    private Context context;
    private List<IngredientStorageModel> ingredientStorageModelList;
    private OnEditListner TheOnEditListener;
    private FirebaseFirestore db;
    String userID;
    private FirebaseAuth mAuth;


    public IngredientStorageAdapter(Context context, List<IngredientStorageModel> ingredientStorageModelList, OnEditListner onEditListener) {
        this.context = context;
        this.ingredientStorageModelList = ingredientStorageModelList;
        this.TheOnEditListener = onEditListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_storage_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.storageItemName.setText(ingredientStorageModelList.get(position).getName());
        holder.storageItemDescription.setText(ingredientStorageModelList.get(position).getDescription());
        holder.storageItemCategory.setText(ingredientStorageModelList.get(position).getCategory());

        holder.storageItemBB.setText(ingredientStorageModelList.get(position).getBestBefore());
        holder.storageItemLocation.setText(ingredientStorageModelList.get(position).getLocation());
        holder.storageItemAmount.setText(ingredientStorageModelList.get(position).getAmount());
        holder.storageItemUnit.setText(ingredientStorageModelList.get(position).getUnit());

        holder.editBtnIStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TheOnEditListener.onEditClick(ingredientStorageModelList.get(position), position);
            }
        });

        holder.delBtnIStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String findID = ingredientStorageModelList.get(position).getDocumentID();
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                userID = mAuth.getCurrentUser().getUid();
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
            }
        });




    }

    @Override
    public int getItemCount() {
        return ingredientStorageModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView storageItemName, storageItemDescription, storageItemCategory;

        TextView storageItemBB, storageItemLocation, storageItemAmount, storageItemUnit;
        Button editBtnIStorage,delBtnIStorage;

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

        }
    }

    public interface OnEditListner {
        void onEditClick(IngredientStorageModel listData, int curPosition);

    }
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
