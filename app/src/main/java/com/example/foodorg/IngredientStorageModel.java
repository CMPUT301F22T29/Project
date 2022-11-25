package com.example.foodorg;

/**
 *  IngredientStorageModel is the model class for the ingredients inside Ingredient Storage
 *  It contains the constructor for this model class which is used in the
 *  Ingredient Storage Adapter class, as well as in Ingredient Storage Activity
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class IngredientStorageModel {

    // The Strings for name, description, best before date,
    // location, amount, unit, category and documentID for
    // the Model class respectively
    String name;
    String description;
    String bestBefore;
    String location;
    String amount;
    String unit;
    String category;
    String documentID;
    private boolean expanded;


    /**
     * @param name The name of the ingredient
     * @param description The description of the ingredient
     * @param bestBefore The best before date of the ingredient
     * @param location The location of the ingredient
     * @param amount The amount of the ingredient
     * @param unit The unit of the ingredient
     * @param category The category of the ingredient
     * @param documentID The documentID of the ingredient
     */
    public IngredientStorageModel(String name, String description, String bestBefore, String location, String amount, String unit, String category, String documentID) {
        this.name = name;
        this.description = description;
        this.bestBefore = bestBefore;
        this.location = location;
        this.amount = amount;
        this.unit = unit;
        this.category = category;
        this.documentID = documentID;
        this.expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name of ingredient
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description name of description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category category of ingredient
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return documentID
     */
    public String getDocumentID() {
        return documentID;
    }

    /**
     * @param documentID documentID of ingredient
     */
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    /**
     * @return bestBefore
     */
    public String getBestBefore() {
        return bestBefore;
    }

    /**
     * @param bestBefore bestBefore of ingredient
     */
    public void setBestBefore(String bestBefore) {
        this.bestBefore = bestBefore;
    }

    /**
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location location of ingredient
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount amount of ingredient
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit unit of ingredient
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
