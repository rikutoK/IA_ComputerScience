package com.example.ia_computerscience.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecipeInfoActivity extends AppCompatActivity {
    private static final String TAG = "RecipeInfoActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);
    }
}