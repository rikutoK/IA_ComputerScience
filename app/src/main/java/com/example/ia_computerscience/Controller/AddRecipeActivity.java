package com.example.ia_computerscience.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddRecipeActivity extends AppCompatActivity {
    private static final String TAG = "AddRecipeActivity";
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Recipe newRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
    }
}