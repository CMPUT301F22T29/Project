package com.example.foodorg;

public class IngredientModel {
    String description;
    String category;
    String documentID;
    String recipeID;

    public IngredientModel(String description, String category, String documentID, String recipeID) {
        this.description = description;
        this.category = category;
        this.documentID = documentID;
        this.recipeID = recipeID;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }
}
