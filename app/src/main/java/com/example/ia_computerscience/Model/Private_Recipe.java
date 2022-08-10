package com.example.ia_computerscience.Model;

import java.io.Serializable;
import java.util.List;

public class Private_Recipe extends Recipe {

    public Private_Recipe() {
        super();
        this.public_ = false;
    }

    public Private_Recipe(String name, String recipeID, String author, String imageID, List<String> ingredients, List<String> steps, int calories, String time, List<FoodType> foodType) {
        super(name, recipeID, author, imageID, ingredients, steps, calories, time, foodType);
        this.public_ = false;
    }

}
