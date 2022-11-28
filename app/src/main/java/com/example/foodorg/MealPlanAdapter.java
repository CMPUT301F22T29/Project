package com.example.foodorg;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.MyViewHolder> {

    private Context context;
    private List<MealPlanModel> mealPlanModelList;


    public MealPlanAdapter(Context context, List<MealPlanModel> mealPlanModelList) {
        // assign each variable
        this.context = context;
        this.mealPlanModelList = mealPlanModelList;

    }

    @NonNull
    @Override
    public MealPlanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mealplan_item, parent, false);
        return new MealPlanAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MealPlanAdapter.MyViewHolder holder, int position) {
        holder.title.setText(mealPlanModelList.get(position).getMealName());
        holder.date.setText(mealPlanModelList.get(position).getDate());
        holder.servings.setText(mealPlanModelList.get(position).getServingsMealPlan());
        Integer whichStore = mealPlanModelList.get(position).getWhichStore();

        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * go to the new activity of containing the ingredients for
             * the respective recipe
             * @param view v
             */
            @Override
            public void onClick(View view) {



                if (whichStore==1){


//                    Intent i = new Intent(context, IngredientOfRecipeActivity.class);
//
//                    i.putExtra("recipe_id", mealPlanModelList.get(position).getMealPlanID());
//                    i.putExtra("key","5");
//                    context.startActivity(i);
                    //Toast.makeText(context, findID, Toast.LENGTH_SHORT).show();


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





    @Override
    public int getItemCount() {
        return mealPlanModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, servings, date;
        Button viewBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_mealplan_item);
            servings = itemView.findViewById(R.id.servingsMealPlanItem);
            date = itemView.findViewById(R.id.dateMealPlanItem);
            viewBtn = itemView.findViewById(R.id.viewRecipeMealPlanBtn);



        }
    }

}
