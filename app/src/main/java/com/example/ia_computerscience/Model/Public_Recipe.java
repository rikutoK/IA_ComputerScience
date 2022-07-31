package com.example.ia_computerscience.Model;

import java.util.List;

public class Public_Recipe extends Recipe {
    private int likes;

    public Public_Recipe(int likes) {
        super();
        this.likes = likes;
    }

    public Public_Recipe(String name, String RECIPE_ID, String author, String imageID, List<String> steps, int calories, int time, List<FoodType> foodType, int likes) {
        super(name, RECIPE_ID, author, imageID, steps, calories, time, foodType);
        this.likes = likes;
    }


    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Public_Recipe{" +
                "likes=" + likes +
                ", RECIPE_ID='" + RECIPE_ID + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", imageID='" + imageID + '\'' +
                ", steps=" + steps +
                ", calories=" + calories +
                ", time=" + time +
                ", foodType=" + foodType +
                '}';
    }
}
