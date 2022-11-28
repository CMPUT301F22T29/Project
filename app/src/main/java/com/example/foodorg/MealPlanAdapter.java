package com.example.foodorg;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 *  MealPlanAdapter is the adapter class for the mealplan inside
 *  MealPLanActivity recyclerview. It contains the constructor for this adapter class.
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.MyViewHolder> {
    /**
     * Constructor for adapter
     * @param context
     * @param mealPlanModelList
     */
    private Context context;
    private List<MealPlanModel> mealPlanModelList;


    public MealPlanAdapter(Context context, List<MealPlanModel> mealPlanModelList) {
        // assign each variable
        this.context = context;
        this.mealPlanModelList = mealPlanModelList;

    }
    /**
     * create the view
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MealPlanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mealplan_item, parent, false);
        return new MealPlanAdapter.MyViewHolder(v);
    }
    /**
     * This method assigns the values to the view variables
     * which were initialized in MyViewHolder
     *
     * @param holder which is the holder for the view
     * @param position which is position of view clicked
     */
    @Override
    public void onBindViewHolder(@NonNull MealPlanAdapter.MyViewHolder holder, int position) {
        holder.title.setText(mealPlanModelList.get(position).getMealName());
        holder.date.setText(mealPlanModelList.get(position).getDate());
        holder.servings.setText(mealPlanModelList.get(position).getServingsMealPlan());
        Integer whichStore = mealPlanModelList.get(position).getWhichStore();






        for (int i = 0; i < mealPlanModelList.size()-1; i++){

            if ((sortlist(mealPlanModelList).get(i).getDate()).equals(sortlist(mealPlanModelList).get(i+1).getDate())){

                Log.d("myTag",sortlist(mealPlanModelList).get(i+1).getDate() +sortlist(mealPlanModelList).get(i+1).getMealName()+sortlist(mealPlanModelList).get(i+1).isExpanded());
                sortlist(mealPlanModelList).get(i+1).setExpanded(true);

            }
        }
        boolean isExpanded = sortlist(mealPlanModelList).get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.GONE: View.VISIBLE);

        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * go to the new activity of containing the ingredients for
             * the respective recipe
             * @param view v
             */
            @Override
            public void onClick(View view) {



                if (whichStore==1){


                    Intent i = new Intent(context, IngredientOfRecipeActivity.class);

                    i.putExtra("recipe_id", mealPlanModelList.get(position).getRecipeID());
                    i.putExtra("key","5");
                    context.startActivity(i);


                }
                else {
                    Intent i = new Intent(context, ShowActivityMealPlan.class);
                    i.putExtra("nameFind",mealPlanModelList.get(position).getMealName());

                    i.putExtra("idmeal",mealPlanModelList.get(position).getMealPlanID());

                    context.startActivity(i);
                }



            }
        });

    }
    /**
     * sort the list according to date
     * @param array
     * @return
     */
    private List<MealPlanModel> sortlist(List<MealPlanModel> array){


        Collections.sort(array, new Comparator<MealPlanModel>() {
            @Override
            public int compare(MealPlanModel t1, MealPlanModel t2) {

                int comparison = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    comparison = sdf.parse(t1.getDate()).compareTo(sdf.parse(t2.getDate()));
                    return comparison;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return comparison;
            }
        });


        return array;
    }





    /**
     * getItemCount method
     * @return the size of the ingredient of recipe list
     */
    @Override
    public int getItemCount() {
        return mealPlanModelList.size();
    }
    /**
     * The MyViewHolder classes extends RecyclerView.ViewHolder to
     * initialize the variables for it based on the id's
     * in the layout
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, servings, date;
        Button viewBtn;
        LinearLayout expandableLayout;
        /**
         * find the items in the view
         * @param itemView which is the view
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_mealplan_item);
            servings = itemView.findViewById(R.id.servingsMealPlanItem);
            date = itemView.findViewById(R.id.dateMealPlanItem);
            viewBtn = itemView.findViewById(R.id.viewRecipeMealPlanBtn);

            expandableLayout = itemView.findViewById(R.id.mealPlanItemAdapter);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MealPlanModel meal1 = mealPlanModelList.get(getAdapterPosition());
                    meal1.setExpanded(!meal1.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });


        }
    }

}
