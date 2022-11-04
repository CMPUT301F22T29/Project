package com.example.foodorg;

/**
 *  RecipeModel is the model class for the recipes inside Recipe
 *  It contains the constructor for this model class which is used in the
 *  Recipe Adapter class, as well as in Recipe Activity
 *
 * @author amman1
 * @author mohaimin
 *
 */
public class RecipeModel {
    // The Strings for title, category, comment, prep, documentID and serving for the Model class
    String title;
    String category;
    String time;
    String servings;
    String comments;
    String documentID;

    /**
     *
     * @param title title
     * @param category category
     * @param time time
     * @param servings servings
     * @param comments comments
     * @param documentID documentID
     */
    public RecipeModel(String title, String category, String time, String servings, String comments, String documentID) {
        this.title = title;
        this.category = category;
        this.time = time;
        this.servings = servings;
        this.comments = comments;
        this.documentID = documentID;
    }

    /**
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     *
     * @return servings
     */
    public String getServings() {
        return servings;
    }

    /**
     *
     * @param servings servings
     */
    public void setServings(String servings) {
        this.servings = servings;
    }

    /**
     *
     * @return comments
     */
    public String getComments() {
        return comments;
    }

    /**
     *
     * @param comments comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     *
     * @return documentID
     */
    public String getDocumentID() {
        return documentID;
    }

    /**
     *
     * @param documentID documentID
     */
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
