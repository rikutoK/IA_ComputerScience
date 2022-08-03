package com.example.ia_computerscience.Controller.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser mUser;

    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtPassword2;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        txtName = findViewById(R.id.SignUp_txtName);
        txtEmail = findViewById(R.id.SignUp_txtEmail);
        txtPassword = findViewById(R.id.SignUp_txtPassword);
        txtPassword2 = findViewById(R.id.SignUp_txtPassword2);
    }

    public void signUp(View view) {
        if(!formValid()) {
            return;
        }

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        //creating user with firebase authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        mUser = mAuth.getCurrentUser();
                        addUser(); //adding the newly created user to database

                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.putExtra(Constants.USER, user);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.w(TAG, task.getException().getMessage(), task.getException());
                    }
                });

    }

    private boolean formValid() {
        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String password2 = txtPassword2.getText().toString();

        if(name.equals("")) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.equals("")) {
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.equals("") || password2.equals("")) {
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!email.contains("@")) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(password2)) {
            Toast.makeText(this, "Password confirmation failed", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addUser() {
        String name = txtName.getText().toString();
        String uid = mUser.getUid();
        String email = mUser.getEmail();

        user = new User(name, uid, email);

        try {
            firestore.collection(Constants.USER).document(uid).set(user);
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(this, "Error adding user to database", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Error adding user to database", e);
        }
    }
}