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
