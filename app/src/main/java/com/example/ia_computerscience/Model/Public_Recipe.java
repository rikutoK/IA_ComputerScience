package com.example.ia_computerscience.Model;

import java.util.List;

public class Public_Recipe extends Recipe {
    private int likes;

    public Public_Recipe(int likes) {
        super();
        this.likes = likes;
    }

    public Public_Recipe(String name, String recipeID, String author, String imageID, List<String> steps, int calories, int time, List<FoodType> foodType, int likes) {
        super(name, recipeID, author, imageID, steps, calories, time, foodType);
        this.likes = likes;
    }


    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
