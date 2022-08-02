package com.example.ia_computerscience.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String name;
    private String userID;
    private String email;
    private List<String> recipeIDs;

    public User() {
        name = null;
        userID = null;
        email = null;
        recipeIDs = new ArrayList<>();
    }

    public User(String name, String userID, String email) {
        this.name = name;
        this.userID = userID;
        this.email = email;
        this.recipeIDs = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
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

    public void addRecipeID(String recipeID) {
        recipeIDs.add(recipeID);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", recipeIDs=" + recipeIDs +
                '}';
    }
}
