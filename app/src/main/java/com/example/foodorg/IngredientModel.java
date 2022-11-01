package com.example.foodorg;

public class IngredientModel {

    String description;
    String category;
    String documentID;

    public IngredientModel(String description, String category, String documentID) {
        this.description = description;
        this.category = category;
        this.documentID = documentID;
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
}
