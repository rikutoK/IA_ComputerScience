package com.example.ia_computerscience.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ia_computerscience.Controller.NavBar.AddRecipeFragment;
import com.example.ia_computerscience.Controller.NavBar.MyRecipeFragment;
import com.example.ia_computerscience.Controller.NavBar.PublicRecipeFragment;
import com.example.ia_computerscience.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private MyRecipeFragment myRecipeFragment;
    private AddRecipeFragment addRecipeFragment;
    private PublicRecipeFragment publicRecipeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myRecipeFragment = new MyRecipeFragment();
        addRecipeFragment = new AddRecipeFragment();
        publicRecipeFragment = new PublicRecipeFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, myRecipeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener((item) -> {
            switch(item.getItemId()) {
                case R.id.MyRecipe:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, myRecipeFragment).commit();
                    return true;
                case R.id.AddRecipe:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, addRecipeFragment).commit();
                    return true;
                case R.id.PublicRecipe:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, publicRecipeFragment).commit();
                    return true;
                default:
                    return false;
            }
        });
    }
}