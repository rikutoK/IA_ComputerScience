package com.example.ia_computerscience.Model;

public enum FoodType {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack"),
    DESSERT("Dessert"),
    DRINK("Drink"),
    SWEET("Sweet"),
    SAVORY("Savory"),
    OTHER("Other");

    private String value;

    FoodType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
