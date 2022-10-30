package com.example.foodorg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder>  {

    private Context context;
    private List<IngredientModel> ingredientModelList;
    private OnEditListner TheOnEditListener;

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.desc.setText(ingredientModelList.get(position).getDescription());
        holder.category.setText(ingredientModelList.get(position).getCategory());

        holder.editbutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TheOnEditListener.onEditClick(ingredientModelList.get(position), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ingredientModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView desc, category;
        Button editbutt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            desc = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            editbutt = itemView.findViewById(R.id.editIngredient);

        }
    }

    public interface OnEditListner {
        void onEditClick(IngredientModel listData, int curPosition);

    }
    public void editDatalist(IngredientModel dataClassObj, int curpos){
        ingredientModelList.get(curpos).setDescription(dataClassObj.getDescription());
        ingredientModelList.get(curpos).setCategory(dataClassObj.getCategory());

        notifyDataSetChanged();
    }
}
