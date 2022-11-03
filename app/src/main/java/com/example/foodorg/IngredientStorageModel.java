package com.example.foodorg;

public class IngredientStorageModel {

    String name;
    String description;

    String bestBefore;
    String location;
    String amount;
    String unit;

    String category;
    String documentID;



    public IngredientStorageModel(String name, String description, String bestBefore, String location, String amount, String unit, String category, String documentID) {
        this.name = name;
        this.description = description;

        this.bestBefore = bestBefore;
        this.location = location;
        this.amount = amount;
        this.unit = unit;

        this.category = category;
        this.documentID = documentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(String bestBefore) {
        this.bestBefore = bestBefore;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
