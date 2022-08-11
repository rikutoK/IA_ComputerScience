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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseUser mUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        if(mUser != null) {
            getUser();
        }
    }

    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void getUser() {
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
        startActivity(intent);
    }
}