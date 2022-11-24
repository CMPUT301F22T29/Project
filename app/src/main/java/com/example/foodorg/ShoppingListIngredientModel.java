package com.example.foodorg;

/**
 *  ShoppingListIngredientModel is the model class for the ingredients inside ShoppingList
 *  It contains the constructor for this model class which is used in the
 *  Shopping List Ingredient Adapter class, as well as in the Shopping List Activity
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class ShoppingListIngredientModel {

    // The Strings name, description, amount, unit, category for the Model class
    String description;
    String category;
    String amount;
    String unit;
    String name;
    private boolean expanded;

    /**
     *
     * @param description description
     * @param category category
     * @param amount amount
     * @param unit unit
     * @param name  name
     */
    public ShoppingListIngredientModel(String description, String category, String amount, String unit, String name) {
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.unit = unit;
        this.name = name;
        this.expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
