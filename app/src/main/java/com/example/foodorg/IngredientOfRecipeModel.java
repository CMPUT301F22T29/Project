package com.example.foodorg;

/**
 *  IngredientOfRecipeModel is the model class for the ingredients inside Recipe
 *  It contains the constructor for this model class which is used in the
 *  Ingredient Of Recipe Adapter class, as well as in Ingredient Of Recipe Activity
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class IngredientOfRecipeModel {
    // The Strings description, documentID, location, amount, unit, category for the Model class
    String description;
    String category;
    String documentID;
    String recipeID;
    String amount;
    String unit;

    /**
     *
     * @param description description
     * @param category category
     * @param documentID documentID
     * @param recipeID recipeID
     * @param amount amount
     * @param unit unit
     */
    public IngredientOfRecipeModel(String description, String category, String documentID, String recipeID, String amount, String unit) {
        this.description = description;
        this.category = category;
        this.documentID = documentID;
        this.recipeID = recipeID;
        this.amount = amount;
        this.unit = unit;
    }

    /**
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return documentID
     */
    public String getDocumentID() {
        return documentID;
    }

    /**
     *
     * @param documentID documentID
     */
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    /**
     *
     * @return recipeID
     */
    public String getRecipeID() {
        return recipeID;
    }

    /**
     *
     * @param recipeID recipeID
     */
    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    /**
     *
     * @return amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     *
     * @param amount amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     *
     * @return unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     *
     * @param unit unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}