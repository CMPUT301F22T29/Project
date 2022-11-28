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
public class RecipeMealPlanAdapter extends RecyclerView.Adapter<RecipeMealPlanAdapter.MyViewHolder> {
    private Context context;
    private static List<RecipeModel> recipeModelList;
    private FirebaseFirestore db;
    String userID;
    private FirebaseAuth FireAuth;
    private FirebaseFirestore Firestoredb;
    private List<MealPlanModel> mealPlanModelList;


    /**
     *
     * @param context context
     * @param recipeModelList list of recipes

     */
    public RecipeMealPlanAdapter(Context context, List<RecipeModel> recipeModelList) {
        this.context = context;
        this.recipeModelList = recipeModelList;

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

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_mealplan_item, parent, false);
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
    public void onBindViewHolder(@NonNull RecipeMealPlanAdapter.MyViewHolder holder, int position) {

        // set The Strings for title, category, comment, prep and serving for the Model class respectively
        holder.title.setText(recipeModelList.get(position).getTitle());
        holder.category.setText(recipeModelList.get(position).getCategory());
        holder.comment.setText(recipeModelList.get(position).getComments());
        holder.prep.setText(recipeModelList.get(position).getTime());
        holder.serving.setText(recipeModelList.get(position).getServings());

        boolean isExpanded = recipeModelList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // editButton listener
        holder.addMealRecipeBtn.setOnClickListener(new View.OnClickListener() {
            /**
             * edit the recipe
             * @param v view
             */@Override
            public void onClick(View v) {
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

        mealPlanModelList = new ArrayList<>();

        final TextView titleRecipe = dialog.findViewById(R.id.nameRecipeMealPlan);
        final EditText servingsMPRecipe= dialog.findViewById(R.id.inputServingsMealPlan);

        titleRecipe.setText(recipeModelList.get(position).getTitle());
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

                DocumentReference relationship = Firestoredb.collection("users")
                        .document(userID).collection("Relationship").document();

                CollectionReference wholerelationship = Firestoredb.collection("users")
                        .document(userID).collection("Relationship");

                String recipeID = recipeModelList.get(position).getDocumentID();

                String idIS = documentReferenceReference.getId();

                String nameMP = recipeModelList.get(position).getTitle();

                String servingsMP = servingsMPRecipe.getText().toString();

                //String daysMP = daysMPRecipe.getText().toString();
                String id = recipeModelList.get(position).getDocumentID();


                int day = datePickerMPRecipeItem.getDayOfMonth();
                int month = datePickerMPRecipeItem.getMonth();
                int year = datePickerMPRecipeItem.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                String bbIS = sdf.format(calendar.getTime());

                String type = "recipe";

                if(TextUtils.isEmpty(nameMP) || TextUtils.isEmpty(servingsMP) ||
                        TextUtils.isEmpty(bbIS) ) {
                    Toast.makeText(context, "Please enter all values", Toast.LENGTH_SHORT).show();
                }

                else{
                    MealPlanModel mealPlanModel = new MealPlanModel(nameMP,bbIS,id,servingsMP,1, idIS);
                    mealPlanModelList.add(mealPlanModel);

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", nameMP);
                    map.put("date", bbIS);
                    map.put("servings", servingsMP);
                    map.put("mealID", idIS);
                    map.put("recipeID", id);
                    map.put("whichStore",1);
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


                    HashMap<String, Object> mapR = new HashMap<>();
                    mapR.put("name", nameMP);
                    mapR.put("date", bbIS);
                    mapR.put("servings", servingsMP);
                    mapR.put("mealID", idIS);

                    mapR.put("actual", "yes");

                    mapR.put("recipeID", recipeID);
                    mapR.put("exist", "must");
                    mapR.put("type", "recipe");
                    relationship.set(mapR)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                /**
                                 * onSuccess will send appropriate Log Message
                                 * @param aVoid an uninstantiable placeholder class to hold a reference
                                 *              to the Class object representing the Java keyword void
                                 */
                                @Override
                                public void onSuccess(Void aVoid) {
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


                    //HashMap<String, Object> toexist = new HashMap<>();
                    //toexist.put("exist", "must");


                    // first set ingredients to must
                    wholerelationship
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                /**
                                 * onComplete method for the task
                                 * @param task which is the task for firestore
                                 */
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    List<String> allRecipeIngredientls = new ArrayList<String>();

                                    for (DocumentSnapshot snapshot1 : task.getResult()){
                                        if ( (String.valueOf(snapshot1.getString("recipe_id")).equals(recipeID)) &
                                                (String.valueOf(snapshot1.getString("type")).equals("ingredientrecipe")) &
                                                (!(String.valueOf(snapshot1.getString("multiple")).equals("yes"))) &
                                                (!allRecipeIngredientls.contains(String.valueOf(snapshot1.getString("ingredientrecipeid")))) ){

                                            allRecipeIngredientls.add(String.valueOf(snapshot1.getString("ingredientrecipeid")));

                                        }
                                    }

                                    int e;

                                    for (e = 0; e < allRecipeIngredientls.size(); e++){

                                        HashMap<String, Object> eachMap = new HashMap<>();

                                        for (DocumentSnapshot snapshot : task.getResult()){

                                            if ( (String.valueOf(snapshot.getString("recipe_id")).equals(recipeID)) &
                                                    (String.valueOf(snapshot.getString("type")).equals("ingredientrecipe")) &
                                                    (String.valueOf(allRecipeIngredientls.get(e)).equals(snapshot.getString("ingredientrecipeid"))) ){

                                                String mealplanID = idIS;
//
                                                eachMap.put("mealid", mealplanID);



                                                eachMap.put("category", snapshot.getString("category"));
                                                eachMap.put("description", snapshot.getString("description"));
                                                eachMap.put("recipe_id", snapshot.getString("recipe_id"));
                                                eachMap.put("type", "ingredientrecipe");

                                                eachMap.put("ingredientrecipeid", allRecipeIngredientls.get(e));
    //                                            eachMap.put("multiple","yes");
    //                                            eachMap.put("unit", String.valueOf(unitcst));

                                                Float portion = Float.valueOf(0);

                                                portion = (Float) Float.valueOf(servingsMP) / Integer.parseInt(snapshot.getString("servingSize"));

                                                Float unitcst = Float.valueOf(0);
                                                unitcst = Float.valueOf(snapshot.getString("amount")) * portion;

                                                Float amountcst = Float.valueOf(0);
                                                amountcst = Float.valueOf(snapshot.getString("unit")) * portion;

                                                eachMap.put("servingSize", snapshot.getString("servingSize"));
                                                eachMap.put("date", bbIS);
                                                eachMap.put("multiple", "yes");

                                                eachMap.put("originalamount", snapshot.getString("amount"));
                                                eachMap.put("originalunit", snapshot.getString("unit"));

                                                eachMap.put("amount",String.valueOf(amountcst));
                                                eachMap.put("unit", snapshot.getString("unit"));


                                            }


                                        }

                                        wholerelationship.document().set(eachMap);
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });

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
        Button addMealRecipeBtn;
        LinearLayout expandableLayout;

        /**
         * find the items in the view
         * @param itemView which is the view
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_recipe_mealplan);
            category = itemView.findViewById(R.id.categor_recipe_mealplan);
            prep = itemView.findViewById(R.id.prep_recipe_mealplan);
            serving = itemView.findViewById(R.id.servings_mealplan);
            comment = itemView.findViewById(R.id.comment_mealplan);
            addMealRecipeBtn = itemView.findViewById(R.id.addRecipeToMealPlan);

            expandableLayout = itemView.findViewById(R.id.recipeOfMealPlanExpandableLayout);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecipeModel recipe = recipeModelList.get(getAdapterPosition());
                    recipe.setExpanded(!recipe.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }



}

