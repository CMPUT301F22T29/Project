package com.example.foodorg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {


    private ArrayList<Ingredient> Data_list;
    private Context context;
    private OnEditListener onEditListener;

    //Adapter constructor
    public IngredientAdapter(ArrayList<Ingredient> data_list, Context context, OnEditListener onEditListener) {
        this.Data_list = data_list;
        this.context = context;
        this.onEditListener = onEditListener;
    }

    //Recyclers view inflater
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
        return  new MyViewHolder(viewItem);
    }


    //objects in the recycler view
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Ingredient classObj = Data_list.get(position);
        holder.name.setText(classObj.getDescription());
        holder.count.setText(classObj.getCount());

        //to delete item from recycler view
        holder.delbutt.setOnClickListener(v->{

            Data_list.remove(position);
            notifyDataSetChanged();


        });
        //run onEditClick method on edit click from main
        holder.editbutt.setOnClickListener(v->{
            onEditListener.onEditClick(Data_list.get(position),position);
        });
    }


    @Override
    public int getItemCount() {
        return Data_list.size();
    }

    //Initializing from cardview.xml
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,count;
        Button editbutt;
        Button delbutt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            count=itemView.findViewById(R.id.count);
            editbutt= itemView.findViewById(R.id.editbutt);
            delbutt=itemView.findViewById(R.id.deletebutt);


        }
    }

    //editing the data list
    public void editDatalist(Ingredient dataClassObj, int curpos){
        Data_list.get(curpos).setDescription(dataClassObj.getDescription());
        Data_list.get(curpos).setCount(dataClassObj.getCount());

        notifyDataSetChanged();
    }

    //interface for on edit click
    public interface OnEditListener{
        void onEditClick(Ingredient listData, int curPosition);

    }


}
