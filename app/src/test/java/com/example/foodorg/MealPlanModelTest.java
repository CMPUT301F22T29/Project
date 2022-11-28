package com.example.foodorg;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class MealPlanModelTest {

    MealPlanModel mealPlanModel = new MealPlanModel("banana", "2021-03-03",
            "xyz123", "2", 2, "abc456");

    MealPlanModel mealPlanModelMock = Mockito.mock(MealPlanModel.class);

    @Test
    public void IngredientCheck(){

        Mockito.when(mealPlanModelMock.getMealName()).thenReturn("banana");
        Mockito.when(mealPlanModelMock.getDate()).thenReturn("2021-03-03");
        Mockito.when(mealPlanModelMock.getRecipeID()).thenReturn("xyz123");
        Mockito.when(mealPlanModelMock.getServingsMealPlan()).thenReturn("2");
        Mockito.when(mealPlanModelMock.getWhichStore()).thenReturn(2);
        Mockito.when(mealPlanModelMock.getMealPlanID()).thenReturn("abc456");

        Assert.assertEquals(mealPlanModel.getMealName(), mealPlanModelMock.getMealName());
        Assert.assertEquals(mealPlanModel.getDate(), mealPlanModelMock.getDate());
        Assert.assertEquals(mealPlanModel.getRecipeID(), mealPlanModelMock.getRecipeID());
        Assert.assertEquals(mealPlanModel.getServingsMealPlan(), mealPlanModelMock.getServingsMealPlan());
        Assert.assertEquals(mealPlanModel.getWhichStore(), mealPlanModelMock.getWhichStore());
        Assert.assertEquals(mealPlanModel.getMealPlanID(), mealPlanModelMock.getMealPlanID());


    }

}
