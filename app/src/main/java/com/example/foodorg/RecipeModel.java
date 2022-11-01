package com.example.foodorg;

public class RecipeModel {
    String title;
    String category;
    String time;
    String servings;
    String comments;
    String documentID;

    public RecipeModel(String title, String category, String time, String servings, String comments, String documentID) {
        this.title = title;
        this.category = category;
        this.time = time;
        this.servings = servings;
        this.comments = comments;
        this.documentID = documentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
