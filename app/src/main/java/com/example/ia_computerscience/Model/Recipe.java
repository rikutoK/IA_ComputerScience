package com.example.ia_computerscience.Model;

import java.util.ArrayList;
import java.util.List;

public abstract class Recipe {
    final protected String RECIPE_ID;

    protected String name;
    protected String author;
    protected String imageID;
    protected List<String> steps;
    protected int calories;
    protected int time;
    protected List<FoodType> foodType;

    public Recipe() {
        name = null;
        RECIPE_ID = null;
        author = null;
        imageID = null;
        steps = new ArrayList<>();
        calories = 0;
        time = 0;
        foodType = new ArrayList<>();
    }

    public Recipe(String name, String RECIPE_ID, String author, String imageID, List<String> steps, int calories, int time, List<FoodType> foodType) {
        this.name = name;
        this.RECIPE_ID = RECIPE_ID;
        this.author = author;
        this.imageID = imageID;
        this.steps = steps;
        this.calories = calories;
        this.time = time;
        this.foodType = foodType;
    }


    protected String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected String getRecipeID() {
        return RECIPE_ID;
    }

    protected String getAuthor() {
        return author;
    }

    protected void setAuthor(String author) {
        this.author = author;
    }

    protected String getImageID() {
        return imageID;
    }

    protected void setImageID(String imageID) {
        this.imageID = imageID;
    }

    protected List<String> getSteps() {
        return steps;
    }

    protected void setSteps(List<String> steps) {
        this.steps = steps;
    }

    protected void addSteps(String step) {
        steps.add(step);
    }

    protected int getCalories() {
        return calories;
    }

    protected void setCalories(int calories) {
        this.calories = calories;
    }

    protected int getTime() {
        return time;
    }

    protected void setTime(int time) {
        this.time = time;
    }

    protected List<FoodType> getFoodType() {
        return foodType;
    }

    protected void setFoodType(List<FoodType> foodType) {
        this.foodType = foodType;
    }

    protected void addFoodType(FoodType type) {
        foodType.add(type);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "RECIPE_ID='" + RECIPE_ID + '\'' +
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
