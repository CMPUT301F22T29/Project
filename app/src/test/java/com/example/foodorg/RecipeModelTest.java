package com.example.foodorg;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class RecipeModelTest {

    RecipeModel recipeModel = new RecipeModel("banana milkshake",
            "fruit", "10", "5", "Easy to make",  "abc456");

    RecipeModel recipeModelMock = Mockito.mock(RecipeModel.class);

    @Test
    public void IngredientCheck(){

        Mockito.when(recipeModelMock.getTitle()).thenReturn("banana milkshake");
        Mockito.when(recipeModelMock.getCategory()).thenReturn("fruit");
        Mockito.when(recipeModelMock.getTime()).thenReturn("10");
        Mockito.when(recipeModelMock.getServings()).thenReturn("5");
        Mockito.when(recipeModelMock.getComments()).thenReturn("Easy to make");
        Mockito.when(recipeModelMock.getDocumentID()).thenReturn("abc456");

        Assert.assertEquals(recipeModel.getTitle(), recipeModelMock.getTitle());
        Assert.assertEquals(recipeModel.getCategory(), recipeModelMock.getCategory());
        Assert.assertEquals(recipeModel.getTime(), recipeModelMock.getTime());
        Assert.assertEquals(recipeModel.getServings(), recipeModelMock.getServings());
        Assert.assertEquals(recipeModel.getComments(), recipeModelMock.getComments());
        Assert.assertEquals(recipeModel.getDocumentID(), recipeModelMock.getDocumentID());


    }

}
