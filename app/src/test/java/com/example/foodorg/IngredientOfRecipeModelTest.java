package com.example.foodorg;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class IngredientOfRecipeModelTest {

    IngredientOfRecipeModel ingredientOfRecipeModel = new IngredientOfRecipeModel("banana",
            "fruit", "xyz123", "abc456", "2",  "2");

    IngredientOfRecipeModel ingredientOfRecipeModelMock = Mockito.mock(IngredientOfRecipeModel.class);

    @Test
    public void IngredientCheck(){

        Mockito.when(ingredientOfRecipeModelMock.getDescription()).thenReturn("banana");
        Mockito.when(ingredientOfRecipeModelMock.getCategory()).thenReturn("fruit");
        Mockito.when(ingredientOfRecipeModelMock.getDocumentID()).thenReturn("xyz123");
        Mockito.when(ingredientOfRecipeModelMock.getRecipeID()).thenReturn("abc456");
        Mockito.when(ingredientOfRecipeModelMock.getAmount()).thenReturn("2");
        Mockito.when(ingredientOfRecipeModelMock.getUnit()).thenReturn("2");

        Assert.assertEquals(ingredientOfRecipeModel.getDescription(), ingredientOfRecipeModelMock.getDescription());
        Assert.assertEquals(ingredientOfRecipeModel.getCategory(), ingredientOfRecipeModelMock.getCategory());
        Assert.assertEquals(ingredientOfRecipeModel.getDocumentID(), ingredientOfRecipeModelMock.getDocumentID());
        Assert.assertEquals(ingredientOfRecipeModel.getRecipeID(), ingredientOfRecipeModelMock.getRecipeID());
        Assert.assertEquals(ingredientOfRecipeModel.getAmount(), ingredientOfRecipeModelMock.getAmount());
        Assert.assertEquals(ingredientOfRecipeModel.getUnit(), ingredientOfRecipeModelMock.getUnit());


    }

}
