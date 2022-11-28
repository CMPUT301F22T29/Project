package com.example.foodorg;
/**
 *  MealPLanModel is the model class for the ingredients and recipes inside MealPlan
 *  It contains the constructor for this model class which is used in the
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class MealPlanModel {

    String mealName;
    String date;
    String servings;
    String mealPlanID;
    String recipeID;
    Integer whichStore;
    private boolean expanded;
    /**
     *
     * @param mealName
     * @param date
     * @param recipeID
     * @param servings
     * @param whichStore
     * @param mealPlanID
     */

    public MealPlanModel(String mealName, String date, String recipeID, String servings, Integer whichStore, String mealPlanID) {
        this.mealName = mealName;
        this.date = date;
        this.mealPlanID = mealPlanID;
        this.servings = servings;
        this.whichStore = whichStore;
        this.recipeID = recipeID;

    }

    /**
     *
     * @return expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     *
     * @param expanded
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
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
     * @param recipeID
     */
    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    /**
     *
     * @return servings
     */
    public String getServingsMealPlan() {
        return servings;
    }

    /**
     *
     * @param servings
     */
    public void setServingsMealPlan(String servings) {
        this.servings = servings;
    }

    /**
     *
     * @return mealName
     */
    public String getMealName() {
        return mealName;
    }

    /**
     *
     * @param mealName
     */
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    /**
     *
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return mealPlanID
     */
    public String getMealPlanID() {
        return mealPlanID;
    }

    /**
     *
     * @param mealPlanID
     */
    public void setMealPlanID(String mealPlanID) {
        this.mealPlanID = mealPlanID;
    }

    /**
     *
     * @return whichStore
     */
    public Integer getWhichStore() {
        return whichStore;
    }

    /**
     *
     * @param whichStore
     */
    public void setWhichStore(Integer whichStore) {
        this.whichStore = whichStore;
    }
}
