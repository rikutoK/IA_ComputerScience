package com.example.ia_computerscience.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ia_computerscience.Model.Public_Recipe;
import com.example.ia_computerscience.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PublicRecipeActivity extends AppCompatActivity {
    private static final String TAG = "PublicRecipeActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private List<Public_Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_recipe);
    }
}