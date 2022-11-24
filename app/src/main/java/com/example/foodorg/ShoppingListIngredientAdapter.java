package com.example.foodorg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ShoppingListIngredientAdapter extends RecyclerView.Adapter<ShoppingListIngredientAdapter.MyViewHolder>{

    private Context context;
    private List<ShoppingListIngredientModel> shoppingListIngredientModelList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    /**
     * This is constructor for the adapter
     * @param context context
     * @param shoppingListIngredientModelList list of the ingredient models
     */
    public ShoppingListIngredientAdapter(Context context, List<ShoppingListIngredientModel> shoppingListIngredientModelList) {
        this.context = context;
        this.shoppingListIngredientModelList = shoppingListIngredientModelList;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppinglistitemview, parent, false);
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
        // set The Strings description, location, amount, unit, category for the Model class respectively
        holder.name.setText(shoppingListIngredientModelList.get(position).getName());
        holder.description.setText(shoppingListIngredientModelList.get(position).getDescription());
        holder.category.setText(shoppingListIngredientModelList.get(position).getCategory());
        holder.unit.setText(shoppingListIngredientModelList.get(position).getUnit());
        holder.amount.setText(shoppingListIngredientModelList.get(position).getAmount());

        boolean isExpanded = shoppingListIngredientModelList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

    }


    /**
     * getItemCount method
     * @return the size of the ingredient of shopping list
     */
    @Override
    public int getItemCount() {
        return shoppingListIngredientModelList.size();
    }

    /**
     * The MyViewHolder classes extends RecyclerView.ViewHolder to
     * initialize the variables for it based on the id's
     * in the layout
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // Create variables for the respective buttons and the textViews on the View
        TextView description, name, category,unit,amount;
        Button delbutt;
        LinearLayout expandableLayout;

        /**
         * find the items in the view
         * @param itemView which is the view
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.shoppingListItemViewName);
            description = itemView.findViewById(R.id.shoppingListItemViewDescription);
            category = itemView.findViewById(R.id.shoppingListItemViewCategory);
            amount= itemView.findViewById(R.id.shoppingListItemViewAmount);
            unit = itemView.findViewById(R.id.shoppingListItemViewUnit);
            delbutt = itemView.findViewById(R.id.shoppingListItemViewDelete);
            expandableLayout = itemView.findViewById(R.id.shoppingListExpandableLayout);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShoppingListIngredientModel ingredient = shoppingListIngredientModelList.get(getAdapterPosition());
                    ingredient.setExpanded(!ingredient.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }
}
