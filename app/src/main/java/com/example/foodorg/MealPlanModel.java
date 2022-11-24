package com.example.foodorg;

public class MealPlanModel {

    String mealName;
    String date;
    String meal;
    String servings;
    String mealPlanID;

    public MealPlanModel(String mealName, String date, String mealPlanID, String servings) {
        this.mealName = mealName;
        this.date = date;
        this.mealPlanID = mealPlanID;
        this.servings = servings;
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
}
