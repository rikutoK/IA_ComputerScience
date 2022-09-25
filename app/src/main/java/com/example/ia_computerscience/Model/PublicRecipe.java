package com.example.ia_computerscience.Model;

import java.io.Serializable;
import java.util.List;

public class PublicRecipe extends Recipe implements Serializable {
    private int likes;

    public PublicRecipe() {
        super();
        this.likes = 1;
        setRecipePublic(true);
    }

    public PublicRecipe(String name, String recipeID, String author, String imageID, List<String> ingredients, List<String> steps, int calories, String time, List<FoodType> foodType) {
        super(name, recipeID, author, imageID, ingredients, steps, calories, time, foodType);
        this.likes = 1;
        setRecipePublic(true);
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
                ", name='" + getName() + '\'' +
                ", recipeID='" + getRecipeID() + '\'' +
                ", author='" + getAuthor() + '\'' +
                ", imageID='" + getImageID() + '\'' +
                ", ingredients=" + getIngredients() +
                ", steps=" + getSteps() +
                ", calories=" + getCalories() +
                ", time=" + getTime() +
                ", foodType=" + getFoodType() +
                '}';
    }
}
