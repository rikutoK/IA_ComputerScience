package com.example.ia_computerscience.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Recipe implements Serializable {
    protected String name;
    protected String recipeID;
    protected String author;
    protected String imageID;
    protected List<String> ingredients;
    protected List<String> steps;
    protected int calories;
    protected String time;
    protected List<FoodType> foodType;

    public Recipe() {
        name = null;
        recipeID = null;
        author = null;
        imageID = null;
        steps = new ArrayList<>();
        calories = 0;
        time = "0 min";
        foodType = new ArrayList<>();
    }

    public Recipe(String name, String recipeID, String author, String imageID, List<String> ingredients, List<String> steps, int calories, String time, List<FoodType> foodType) {
        this.name = name;
        this.recipeID = recipeID;
        this.author = author;
        this.imageID = imageID;
        this.ingredients = ingredients;
        this.steps = steps;
        this.calories = calories;
        this.time = time;
        this.foodType = foodType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageID() {
        return imageID;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(String ingredient) {
        ingredients.add(ingredient);
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public void addStep(String step) {
        steps.add(step);
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<FoodType> getFoodType() {
        return foodType;
    }

    public void setFoodType(List<FoodType> foodType) {
        this.foodType = foodType;
    }

    public void addFoodType(FoodType type) {
        foodType.add(type);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
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
