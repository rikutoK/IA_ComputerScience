package com.example.ia_computerscience.Model;

import java.io.Serializable;
import java.util.List;

public class Public_Recipe extends Recipe implements Serializable {
    private int likes;

    public Public_Recipe() {
        super();
        this.likes = 1;
        this.public_ = true;
    }

    public Public_Recipe(String name, String recipeID, String author, String imageID, List<String> ingredients, List<String> steps, int calories, String time, List<FoodType> foodType) {
        super(name, recipeID, author, imageID, ingredients, steps, calories, time, foodType);
        this.likes = 1;
        this.public_ = true;
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
                ", name='" + name + '\'' +
                ", recipeID='" + recipeID + '\'' +
                ", author='" + author + '\'' +
                ", imageID='" + imageID + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                ", calories=" + calories +
                ", time=" + time +
                ", foodType=" + foodType +
                '}';
    }
}
