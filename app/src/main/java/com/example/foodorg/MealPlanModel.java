package com.example.foodorg;

public class MealPlanModel {

    String mealName;
    String date;
    String servings;
    String mealPlanID;
    String recipeID;
    Integer whichStore;
    private boolean expanded;

    public MealPlanModel(String mealName, String date, String recipeID, String servings, Integer whichStore, String mealPlanID) {
        this.mealName = mealName;
        this.date = date;
        this.mealPlanID = mealPlanID;
        this.servings = servings;
        this.whichStore = whichStore;
        this.recipeID = recipeID;

    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public String getServingsMealPlan() {
        return servings;
    }

    public void setServingsMealPlan(String servings) {
        this.servings = servings;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMealPlanID() {
        return mealPlanID;
    }

    public void setMealPlanID(String mealPlanID) {
        this.mealPlanID = mealPlanID;
    }

    public Integer getWhichStore() {
        return whichStore;
    }

    public void setWhichStore(Integer whichStore) {
        this.whichStore = whichStore;
    }
}
