package com.example.ia_computerscience.Model;

import java.util.List;

public class User {
    final private String USER_ID;

    private String name;
    private String email;
    private List<String> recipeIDs;

    public User() {
        name = null;
        USER_ID = null;
        email = null;
        recipeIDs = null;
    }

    public User(String name, String USER_ID, String email, List<String> recipeIDs) {
        this.name = name;
        this.USER_ID = USER_ID;
        this.email = email;
        this.recipeIDs = recipeIDs;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return USER_ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRecipeIDs() {
        return recipeIDs;
    }

    public void setRecipeIDs(List<String> recipeIDs) {
        this.recipeIDs = recipeIDs;
    }

    @Override
    public String toString() {
        return "User{" +
                "USER_ID='" + USER_ID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", recipeIDs=" + recipeIDs +
                '}';
    }
}
