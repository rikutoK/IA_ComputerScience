package com.example.ia_computerscience.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ia_computerscience.R;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private EditText txtEmail;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.Login_txtEmail);
        txtPassword = findViewById(R.id.Login_txtPassword);
    }
    
    public void login(View view) {
        if(!formValid()) {
            return;
        }

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                    }
                    else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.w(TAG, task.getException().getMessage(), task.getException());
                    }
                });
    }

    private boolean formValid() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if(email.equals("")) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.equals("")) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!email.contains("@")) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    
    
}