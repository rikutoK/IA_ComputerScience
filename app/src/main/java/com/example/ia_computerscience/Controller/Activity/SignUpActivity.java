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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser mUser;

    private TextInputLayout txtName;
    private TextInputLayout txtEmail;
    private TextInputLayout txtPassword;
    private TextInputLayout txtPassword2;

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

        String email = txtEmail.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        //creating user with firebase authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        mUser = mAuth.getCurrentUser();
                        addUser(); //adding the newly created user to database

                        goTOHomeActivity();
                    }
                    else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.w(TAG, task.getException().getMessage(), task.getException());
                    }
                });

    }

    private boolean formValid() {
        String name = txtName.getEditText().getText().toString();
        String email = txtEmail.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();
        String password2 = txtPassword2.getEditText().getText().toString();

        if(name.equals("")) {
            txtName.setError("Name is empty");
            return false;
        }
        else {
            txtName.setError(null);
        }

        if(email.equals("")) {
            txtEmail.setError("Email is empty");
            return false;
        }
        if(!email.contains("@")) {
            txtEmail.setError("Invali Email");
            return false;
        }
        else {
            txtEmail.setError(null);
        }

        if(password.equals("")) {
            txtPassword.setError("Password is empty");
            return false;
        }
        if(password2.equals("")) {
            txtPassword2.setError("Password is empty");
            return false;
        }
        if(!password.equals(password2)) {
            txtPassword.setError("Password confirmation failed");
            txtPassword2.setError("Password confirmation failed");
            return false;
        }
        else {
            txtPassword.setError(null);
            txtPassword2.setError(null);
        }

        return true;
    }

    private void addUser() {
        String name = txtName.getEditText().getText().toString();
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

    private void goTOHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.USER, user);

        if(getIntent().hasExtra(Constants.RECIPE_ID)) {
            intent.putExtra(Constants.RECIPE_ID, getIntent().getStringExtra(Constants.RECIPE_ID));
        }

        startActivity(intent);
    }
}