package com.example.ia_computerscience.Model;

import java.util.List;

public class Private_Recipe extends Recipe {

    public Private_Recipe() {
        super();
    }

    public Private_Recipe(String name, String RECIPE_ID, String author, String imageID, List<String> ingredients, List<String> steps, int calories, int time, List<FoodType> foodType) {
        super(name, RECIPE_ID, author, imageID, ingredients, steps, calories, time, foodType);
    }
}
