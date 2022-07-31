package com.example.ia_computerscience.Model;

import java.util.List;

public class Private_Recipe extends Recipe {

    public Private_Recipe() {
        super();
    }

    public Private_Recipe(String name, String recipeID, String author, String imageID, List<String> steps, int calories, int time, List<FoodType> foodType) {
        super(name, recipeID, author, imageID, steps, calories, time, foodType);
    }
}
