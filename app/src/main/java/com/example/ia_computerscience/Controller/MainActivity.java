package com.example.ia_computerscience.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.ia_computerscience.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}