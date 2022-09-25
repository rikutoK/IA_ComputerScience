package com.example.ia_computerscience.Model;

import java.util.List;

public class PrivateRecipe extends Recipe {

    public PrivateRecipe() {
        super();
    }

    public PrivateRecipe(String name, String recipeID, String author, String imageID, List<String> ingredients, List<String> steps, int calories, String time, List<FoodType> foodType) {
        super(name, recipeID, author, imageID, ingredients, steps, calories, time, foodType);
        setRecipePublic(false);
    }

}
