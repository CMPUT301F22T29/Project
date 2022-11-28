package com.example.foodorg;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class IngredientStorageModelTest {

    IngredientStorageModel ingredientStorageModel = new IngredientStorageModel("banana", "banana",
            "2021-03-03", "fridge", "2", "2", "fruit", "xyz123");

    IngredientStorageModel ingredientStorageModelMock = Mockito.mock(IngredientStorageModel.class);

    @Test
    public void IngredientCheck(){

        Mockito.when(ingredientStorageModelMock.getName()).thenReturn("banana");
        Mockito.when(ingredientStorageModelMock.getDescription()).thenReturn("banana");
        Mockito.when(ingredientStorageModelMock.getBestBefore()).thenReturn("2021-03-03");
        Mockito.when(ingredientStorageModelMock.getLocation()).thenReturn("fridge");
        Mockito.when(ingredientStorageModelMock.getAmount()).thenReturn("2");
        Mockito.when(ingredientStorageModelMock.getUnit()).thenReturn("2");
        Mockito.when(ingredientStorageModelMock.getCategory()).thenReturn("fruit");
        Mockito.when(ingredientStorageModelMock.getDocumentID()).thenReturn("xyz123");

        Assert.assertEquals(ingredientStorageModel.getName(), ingredientStorageModelMock.getName());
        Assert.assertEquals(ingredientStorageModel.getDescription(), ingredientStorageModelMock.getDescription());
        Assert.assertEquals(ingredientStorageModel.getBestBefore(), ingredientStorageModelMock.getBestBefore());
        Assert.assertEquals(ingredientStorageModel.getLocation(), ingredientStorageModelMock.getLocation());
        Assert.assertEquals(ingredientStorageModel.getAmount(), ingredientStorageModelMock.getAmount());
        Assert.assertEquals(ingredientStorageModel.getUnit(), ingredientStorageModelMock.getUnit());
        Assert.assertEquals(ingredientStorageModel.getCategory(), ingredientStorageModelMock.getCategory());
        Assert.assertEquals(ingredientStorageModel.getDocumentID(), ingredientStorageModelMock.getDocumentID());


    }

}
