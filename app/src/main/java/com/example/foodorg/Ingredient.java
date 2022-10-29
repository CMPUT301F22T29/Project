package com.example.foodorg;




public class Ingredient {

    //Food Book class
    String description,count;

    public Ingredient(){}

    public Ingredient(String description, String count) {
        this.description = description;
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    //Getters and setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

