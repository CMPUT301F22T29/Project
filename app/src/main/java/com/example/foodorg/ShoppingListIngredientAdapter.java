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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 *  ShoppingListIngredientAdapter is the adapter class for the ingredients inside
 *  Shopping List Ingredient recyclerview. It contains the constructor for this adapter class
 *  which is used in the Shopping list Activity
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class ShoppingListIngredientAdapter extends RecyclerView.Adapter<ShoppingListIngredientAdapter.MyViewHolder>{

    private Context context;
    private List<ShoppingListIngredientModel> shoppingListIngredientModelList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID;


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

        // set The Strings description, amount, unit, category and name for the Model class respectively
        holder.name.setText(shoppingListIngredientModelList.get(position).getName());
        holder.description.setText(shoppingListIngredientModelList.get(position).getDescription());
        holder.category.setText(shoppingListIngredientModelList.get(position).getCategory());
        holder.unit.setText(shoppingListIngredientModelList.get(position).getUnit());
        holder.amount.setText(shoppingListIngredientModelList.get(position).getAmount());

        // boolean to click on view and expand it if necessary
        boolean isExpanded = shoppingListIngredientModelList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // button that adds ingredient from shopping list to ingredien storage
        holder.addbutt.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view
             */
            @Override
            public void onClick(View view) {

                showCustomDialog(position);

            }
        });

    }

    /**
     * Custom dialog that adds ingredient from shopping list to ingredient storage
     * @param position
     */
    private void showCustomDialog(int position){

        // initialize the dialog, its settings and layout
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.shoppinglistiteminput);

        // EditTexts that take the remaining necessary parameters
        EditText locationedt = dialog.findViewById(R.id.locationInputShoppingList);
        EditText amountedt = dialog.findViewById(R.id.amountInputShoppingList);
        EditText unitedt = dialog.findViewById(R.id.unitInputShoppingList);

        // Spinner for the date
        DatePicker datePickerShoppingList = dialog.findViewById(R.id.datePickerAddShoppingListItem);

        // Get the description and category, which remain unchanged
        String description = shoppingListIngredientModelList.get(position).getDescription();
        String category = shoppingListIngredientModelList.get(position).getCategory();

        // Close the dialog listener
        final ImageView closeMealPlanAlert = dialog.findViewById(R.id.closeAlertInputShoppingList);
        closeMealPlanAlert.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Button that confirms adding the ingredient
        Button addButton = dialog.findViewById(R.id.btnAddShoppingList);

        // Listener for confirm button
        addButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view
             */
            @Override
            public void onClick(View view) {

                // Initialize the firebase references
                mAuth = FirebaseAuth.getInstance();
                db = FirebaseFirestore.getInstance();
                userID = mAuth.getCurrentUser().getUid();

                // Document references needed to refer to ingredient storage
                DocumentReference documentReferenceReference = db.collection("users")
                        .document(userID).collection("Ingredient_Storage").document();

                // Collection references needed to refer to ingredient storage
                CollectionReference ingredientCollection = db.collection("users")
                        .document(userID).collection("Ingredient_Storage");

                DocumentReference relationship = db.collection("users")
                        .document(userID).collection("Relationship").document();

                // Collection reference to refer to relationship collection with all the relationships
                CollectionReference wholerelationship = db.collection("users")
                        .document(userID).collection("Relationship");

                // get the id of the document
                String idIS = documentReferenceReference.getId();

                // get the String values of the location, amount & unit
                String location = locationedt.getText().toString();
                String amount = amountedt.getText().toString();
                String unit = unitedt.getText().toString();

                // date picker values
                int day = datePickerShoppingList.getDayOfMonth();
                int month = datePickerShoppingList.getMonth();
                int year = datePickerShoppingList.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                // get the formatted date values
                String bbIS = sdf.format(calendar.getTime());

                //Error checking for missing fields on edit
                if (TextUtils.isEmpty(location) || TextUtils.isEmpty(amount) || TextUtils.isEmpty(unit)) {
                    Toast.makeText(context, "Please enter all values", Toast.LENGTH_SHORT).show();
                } else{



                    ingredientCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            String uu = "";


                            for (DocumentSnapshot snapshot : task.getResult()){

                                if ((String.valueOf(snapshot.get("unit")).equals(unit)) &
                                        (String.valueOf(snapshot.get("location")).equals(location)) &
                                        (String.valueOf(snapshot.getString("description")).equals(description)) &
                                        (String.valueOf(snapshot.getString("category")).equals(category))){

                                    uu = "yes";

                                    HashMap<String, Object> mapR = new HashMap<>();

                                    mapR.put("name", description);
                                    mapR.put("description", description);
                                    mapR.put("bestBefore", bbIS);
                                    mapR.put("category", category);

                                    // Float values that get the value of the amount & units

                                    Integer amountt = Integer.valueOf(0);

                                    amountt += Integer.parseInt(String.valueOf(snapshot.get("amount")));

                                    amountt += Integer.parseInt(amount);

                                    mapR.put("amount", amountt);

                                    ingredientCollection.document(snapshot.getId()).update(mapR);

                                }

                            }

                            // if ingredient does not exist, we then just add the ingredient accordingly
                            if (uu.equals("")){

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("name", description);
                                map.put("description", description);
                                map.put("bestBefore", bbIS);
                                map.put("location", location);
                                map.put("amount", Integer.parseInt(amount));
                                map.put("unit", Integer.parseInt(unit));
                                map.put("category", category);
                                map.put("id", idIS);

                                ingredientCollection.document().set(map);
                            }

                            shoppingListIngredientModelList.remove(position);
                            Log.d(TAG, " has been deleted successfully!");
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });



                    // also add the ingredient to relationship collection for reference
                    wholerelationship.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        /**
                         *
                         * @param task
                         */
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            // String jj checks if ingredient already exists
                            String jj = "";

                            for (DocumentSnapshot snapshot : task.getResult()){

                                // if ingredient does exist, we then just update the ingredient accordingly
                                if ((String.valueOf(snapshot.getString("type")).equals("ingredient")) &
                                        (String.valueOf(snapshot.get("unit")).equals(unit)) &
                                        (String.valueOf(snapshot.get("location")).equals(location)) &
                                        (String.valueOf(snapshot.getString("description")).equals(description)) &
                                        (String.valueOf(snapshot.getString("category")).equals(category)) &
                                (String.valueOf(snapshot.getString("exist")).equals("yes")) ) {


                                    jj = "yes";

                                    HashMap<String, Object> mapR = new HashMap<>();

                                    mapR.put("name", description);
                                    mapR.put("description", description);
                                    mapR.put("bestBefore", bbIS);
                                    mapR.put("category", category);

                                    // Float values that get the value of the amount & units
                                    Float unitt = Float.valueOf(0);
                                    Float amountt = Float.valueOf(0);

                                    //unitt += Float.parseFloat(String.valueOf(snapshot.get("unit")));
                                    amountt += Float.parseFloat(String.valueOf(snapshot.get("amount")));

                                    //unitt += Float.parseFloat(unit);
                                    amountt += Float.parseFloat(amount);

                                    //mapR.put("unit", unitt);
                                    mapR.put("amount", amountt);

                                    mapR.put("type", "ingredient");
                                    mapR.put("exist", "yes");

                                    wholerelationship.document(snapshot.getId()).update(mapR);

                                }

                            }

                            // if ingredient does not exist, we then just add the ingredient accordingly
                            if (jj.equals("")){
                                HashMap<String, Object> mapk = new HashMap<>();

                                mapk.put("name", description);
                                mapk.put("description", description);
                                mapk.put("bestBefore", bbIS);
                                mapk.put("location", location);
                                mapk.put("category", category);


                                mapk.put("unit", Float.parseFloat(unit));
                                mapk.put("amount", Float.parseFloat(amount));

                                mapk.put("type", "ingredient");
                                mapk.put("exist", "yes");

                                wholerelationship.document().set(mapk);
                            }

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
        Button delbutt, addbutt;
        LinearLayout expandableLayout;

        /**
         * find the items in the view
         * @param itemView which is the view
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // views for the name (description essentially), description,
            // category, amount, unit and expandable layout
            name = itemView.findViewById(R.id.shoppingListItemViewName);
            description = itemView.findViewById(R.id.shoppingListItemViewDescription);
            category = itemView.findViewById(R.id.shoppingListItemViewCategory);
            amount= itemView.findViewById(R.id.shoppingListItemViewAmount);
            unit = itemView.findViewById(R.id.shoppingListItemViewUnit);
            expandableLayout = itemView.findViewById(R.id.shoppingListExpandableLayout);

            // add button
            addbutt = itemView.findViewById(R.id.shoppingListItemViewAdd);

            // description listener to expand the view
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
