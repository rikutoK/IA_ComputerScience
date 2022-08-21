package com.example.ia_computerscience.Controller.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ia_computerscience.Controller.NavBar.AccountFragment;
import com.example.ia_computerscience.Controller.NavBar.AddRecipeFragment;
import com.example.ia_computerscience.Controller.NavBar.MyRecipeFragment;
import com.example.ia_computerscience.Controller.NavBar.PublicRecipeFragment;
import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private BottomNavigationView bottomNavigationView;

    private MyRecipeFragment myRecipeFragment;
    private AddRecipeFragment addRecipeFragment;
    private PublicRecipeFragment publicRecipeFragment;
    private AccountFragment accountFragment;

    private User user;
    
    private boolean link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        if(getIntent().hasExtra(Constants.USER)) {
            user = (User) getIntent().getSerializableExtra(Constants.USER);
            Log.d(TAG, "onCreate: ");
            if(getIntent().hasExtra(Constants.RECIPE_ID)) {
                String recipeID = (String) getIntent().getStringExtra(Constants.RECIPE_ID);
                myRecipeFragment = MyRecipeFragment.newInstance(user, recipeID);
            }
            else {
                myRecipeFragment = MyRecipeFragment.newInstance(user);
            }

            addRecipeFragment = AddRecipeFragment.newInstance(user);
            publicRecipeFragment = PublicRecipeFragment.newInstance(user);
            accountFragment = AccountFragment.newInstance(user);
        }
        else {
            myRecipeFragment = new MyRecipeFragment();
            addRecipeFragment = new AddRecipeFragment();
            publicRecipeFragment = new PublicRecipeFragment();
            accountFragment = new AccountFragment();
        }


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
                case R.id.Account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, accountFragment).commit();
                    return true;
                default:
                    return false;
            }
        });
    }
}