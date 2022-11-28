package com.example.foodorg;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
public class IngredientMealPlanAdapter extends RecyclerView.Adapter<IngredientMealPlanAdapter.MyViewHolder>  {

    // Initialize the Context, the ingredient storage list for the ingredient storage models,
    // the onEditListener method for editing the adapter, the Firestore database,
    // the userID and the FireBase Authentication
    private Context context;
    private static List<IngredientStorageModel> ingredientStorageModelList;
    private FirebaseFirestore db;
    private String userID;

    private FirebaseAuth FireAuth;
    private FirebaseFirestore Firestoredb;
    private List<MealPlanModel> mealPlanModelList;


    /**
     *
     * @param context context for the adapter
     * @param ingredientStorageModelList list for the ingredient models

     */
    public IngredientMealPlanAdapter(Context context, List<IngredientStorageModel> ingredientStorageModelList) {
        // assign each variable
        this.context = context;
        this.ingredientStorageModelList = ingredientStorageModelList;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_mealplan_item, parent, false);
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
        holder.addToMPBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * this will initialize the method to edit the view
             * @param v which is the view
             */
            @Override
            public void onClick(View v) {
                //TheOnEditListener.onEditClick(ingredientStorageModelList.get(position), position);
                showCustomDialog(position);
            }
        });

    }


    private void showCustomDialog(int position){
        // initialize the dialog, its settings and layout
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.mealplan_recipe_input_dialog);

        //ingredientStorageModelList = new ArrayList<>();
        mealPlanModelList = new ArrayList<>();

        final TextView titleRecipe = dialog.findViewById(R.id.nameRecipeMealPlan);
        final EditText servingsMPRecipe= dialog.findViewById(R.id.inputServingsMealPlan);

        servingsMPRecipe.setVisibility(View.INVISIBLE);

        titleRecipe.setText(ingredientStorageModelList.get(position).getName());

        String theunit = ingredientStorageModelList.get(position).getUnit();
        String theamount = ingredientStorageModelList.get(position).getAmount();
        String thedescription = ingredientStorageModelList.get(position).getDescription();
        String thebb = ingredientStorageModelList.get(position).getBestBefore();
        String thelocation = ingredientStorageModelList.get(position).getLocation();
        String thecategory = ingredientStorageModelList.get(position).getCategory();

        final DatePicker datePickerMPRecipeItem = dialog.findViewById(R.id.datePickerMPRecipe);

        final ImageView closeMealPlanAlert = dialog.findViewById(R.id.closeAlertMealPlan);
        closeMealPlanAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button submitButton = dialog.findViewById(R.id.addRecipeToMealPlanFSBtn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FireAuth = FirebaseAuth.getInstance();
                Firestoredb = FirebaseFirestore.getInstance();
                userID = FireAuth.getCurrentUser().getUid();
                DocumentReference documentReferenceReference = Firestoredb.collection("users")
                        .document(userID).collection("MealPlan").document();

                String idIS = documentReferenceReference.getId();

                String descriptionMP = ingredientStorageModelList.get(position).getDescription();

                String bestbefore = ingredientStorageModelList.get(position).getBestBefore();
                String location = ingredientStorageModelList.get(position).getLocation();

                //String servingsMP = servingsMPRecipe.getText().toString();
                String id = ingredientStorageModelList.get(position).getDocumentID();

                DocumentReference relationship = Firestoredb.collection("users")
                        .document(userID).collection("Relationship").document();

                CollectionReference wholerelationship = Firestoredb.collection("users")
                        .document(userID).collection("Relationship");

                int day = datePickerMPRecipeItem.getDayOfMonth();
                int month = datePickerMPRecipeItem.getMonth();
                int year = datePickerMPRecipeItem.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String bbIS = sdf.format(calendar.getTime());

                if(TextUtils.isEmpty(bbIS) ) {
                    Toast.makeText(context, "Please enter all values", Toast.LENGTH_SHORT).show();
                }

                else{
                    MealPlanModel mealPlanModel = new MealPlanModel(descriptionMP,bbIS,id,"1",2, idIS);
                    mealPlanModelList.add(mealPlanModel);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("description", descriptionMP);
                    map.put("date", bbIS);
                    map.put("servings", "1");
                    map.put("name", descriptionMP);
                    map.put("mealID", idIS);
                    map.put("recipeID", id);
                    map.put("whichStore",2);
                    documentReferenceReference.set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                /**
                                 * onSuccess will send appropriate Log Message
                                 * @param aVoid an uninstantiable placeholder class to hold a reference
                                 *              to the Class object representing the Java keyword void
                                 */
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    //ingredientStorageAdapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

//                    int num = 0;
//
//                    num = Integer.parseInt(servingsMP);
//                    int u;

//                    for (u = 0; u<num; u++) {
//
//
//
//                        wholerelationship.document().set(mapS);
//                    }

                    HashMap<String, Object> mapS = new HashMap<>();
                    mapS.put("description", thedescription);
                    mapS.put("amount", theamount);
                    mapS.put("unit", theunit);
                    mapS.put("category", thecategory);

                    mapS.put("date", bbIS);
                    mapS.put("servings", "1");
                    mapS.put("mealID", idIS);
                    mapS.put("exist", "must");
                    mapS.put("multiple", "no");
                    mapS.put("type", "ingredient");
                    mapS.put("id", id);
                    mapS.put("bb", bestbefore);
                    mapS.put("location", location);

                    relationship.set(mapS);


//                    wholerelationship
//                            .get()
//                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                /**
//                                 * onComplete method for the task
//                                 * @param task which is the task for firestore
//                                 */
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//
//                                    for (DocumentSnapshot snapshot : task.getResult()){
//                                        if ((String.valueOf(snapshot.getString("description")).equals(descriptionMP)) &
//                                                (String.valueOf(snapshot.getString("exist")).equals("must")) &
//                                                (String.valueOf(snapshot.getString("type")).equals("ingredient"))){
//
//                                            wholerelationship.document(snapshot.getId()).update("exist", "must");
//
//                                            String meal = id;
//                                            String theAmount = snapshot.getString("amount");
//                                            String theCategory = snapshot.getString("category");
//                                            String theDescription = snapshot.getString("description");
//                                            String theRecipeID = snapshot.getString("recipe_id");
//                                            String theType = snapshot.getString("type");
//                                            String theUnit = snapshot.getString("unit");
//                                            String serving = snapshot.getString("servingSize");
//                                            String mealplanID = id;
//
//                                            Float portion = Float.valueOf(0);
//                                            portion = (float)Integer.parseInt(servingsMP) / Integer.parseInt(serving);
//
//                                            Float unitcst = Float.valueOf(0);
//                                            unitcst = (float)Integer.parseInt(theUnit) * portion;
//
//                                            Float amountcst = Float.valueOf(0);
//                                            amountcst = (float)Integer.parseInt(theAmount) * portion;
//
//                                            //Integer unitcst = Integer.parseInt(theUnit);
//                                            //Integer amountcst = Integer.parseInt(theAmount);
//
//                                            //for (f= 0; f < len; f++){
//
//                                            HashMap<String, Object> eachMap = new HashMap<>();
//
//                                            eachMap.put("mealid", mealplanID);
//                                            eachMap.put("amount", (double)amountcst);
//                                            eachMap.put("category", theCategory);
//                                            eachMap.put("description", theDescription);
//                                            eachMap.put("recipe_id", theRecipeID);
//                                            eachMap.put("type", theType);
//                                            eachMap.put("multiple","yes");
//                                            eachMap.put("unit", (double)unitcst);
//                                            eachMap.put("servingSiz", serving);
//
//                                            wholerelationship.document().set(eachMap);
//
//                                            //}
//
//                                        }
//                                    }
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error deleting document", e);
//                                }
//                            });

                }

            }
        });

        dialog.show();



        // The below are code to set dialog to 80% of the screen
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        int dialogWindowHeight = (int) (displayHeight * 0.7f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        dialog.getWindow().setAttributes(layoutParams);



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
        Button addToMPBtn;

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

            addToMPBtn = itemView.findViewById(R.id.addIngredientToMealPlanBtn);
            //delBtnIStorage = itemView.findViewById(R.id.deleteIngredient);

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



}
