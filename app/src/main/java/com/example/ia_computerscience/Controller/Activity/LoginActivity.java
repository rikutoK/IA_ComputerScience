package com.example.ia_computerscience.Controller.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private TextInputLayout txtEmail;
    private TextInputLayout txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        txtEmail = findViewById(R.id.Login_txtName);
        txtPassword = findViewById(R.id.Login_txtPassword);
    }
    
    public void login(View view) {
        if(!formValid()) {
            return;
        }

        String email = txtEmail.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        getUser(); //gets user from db and moves to next activity
                    }
                    else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.w(TAG, task.getException().getMessage(), task.getException());
                    }
                });
    }

    private boolean formValid() {
        String email = txtEmail.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        if(email.equals("")) {
            txtEmail.setError("Email is empty");
            return false;
        }
        else if(!email.contains("@")) {
            txtEmail.setError("Invalid Email");
            return false;
        }
        else {
            txtEmail.setError(null);
        }

        if(password.equals("")) {
            txtPassword.setError("Password is empty");
            return false;
        }
        else {
            txtPassword.setError(null);
        }

        return true;
    }
    
    private void getUser() {
        FirebaseUser mUser = mAuth.getCurrentUser();

        firestore.collection(Constants.USER).document(mUser.getUid()).get()
                .addOnCompleteListener(this, task -> {
                   if(task.isSuccessful()) {
                       User user = task.getResult().toObject(User.class);
                       goTOHomeActivity(user);
                       Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                   }
                   else {
                       Log.w(TAG, task.getException().getMessage(), task.getException());
                       Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                   }
                });
    }

    private void goTOHomeActivity(User user) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.USER, user);

        if(getIntent().hasExtra(Constants.RECIPE_ID)) {
            intent.putExtra(Constants.RECIPE_ID, getIntent().getStringExtra(Constants.RECIPE_ID));
        }

        startActivity(intent);
    }
}