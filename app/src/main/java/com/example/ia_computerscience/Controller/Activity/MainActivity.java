package com.example.ia_computerscience.Controller.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);

        if(getIntent().hasExtra(Constants.RECIPE_ID)) {
            intent.putExtra(Constants.RECIPE_ID, getIntent().getStringExtra(Constants.RECIPE_ID));
        }

        startActivity(intent);
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);

        if(getIntent().hasExtra(Constants.RECIPE_ID)) {
            intent.putExtra(Constants.RECIPE_ID, getIntent().getStringExtra(Constants.RECIPE_ID));
        }

        startActivity(intent);
    }
}