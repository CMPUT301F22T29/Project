package com.example.foodorg;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ShoppingListIngredientModelTest {

    ShoppingListIngredientModel shoppingListIngredientModel = new ShoppingListIngredientModel("banana milkshake",
            "fruit", "10", "5", "banana milkshake");

    ShoppingListIngredientModel shoppingListIngredientModelMock = Mockito.mock(ShoppingListIngredientModel.class);

    @Test
    public void IngredientCheck(){

        Mockito.when(shoppingListIngredientModelMock.getDescription()).thenReturn("banana milkshake");
        Mockito.when(shoppingListIngredientModelMock.getCategory()).thenReturn("fruit");
        Mockito.when(shoppingListIngredientModelMock.getUnit()).thenReturn("10");
        Mockito.when(shoppingListIngredientModelMock.getAmount()).thenReturn("5");
        Mockito.when(shoppingListIngredientModelMock.getName()).thenReturn("banana milkshake");

        Assert.assertEquals(shoppingListIngredientModel.getDescription(), shoppingListIngredientModelMock.getDescription());
        Assert.assertEquals(shoppingListIngredientModel.getCategory(), shoppingListIngredientModelMock.getCategory());
        Assert.assertEquals(shoppingListIngredientModel.getUnit(), shoppingListIngredientModelMock.getUnit());
        Assert.assertEquals(shoppingListIngredientModel.getAmount(), shoppingListIngredientModelMock.getAmount());
        Assert.assertEquals(shoppingListIngredientModel.getName(), shoppingListIngredientModelMock.getName());


    }

}
