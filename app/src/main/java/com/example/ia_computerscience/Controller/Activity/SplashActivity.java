package com.example.ia_computerscience.Controller.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private FirebaseUser mUser;
    private FirebaseFirestore firestore;

    private String recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        recipeID = "";
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(task.getResult() != null) {
                            Uri deeplink = task.getResult().getLink();

                            recipeID = deeplink.getQueryParameter(Constants.RECIPE_ID);
                            Log.d(TAG, "onCreate: "+recipeID);
                        }

                        if(mUser != null) {
                            getUser();
                        }
                        else {
                            goToMainActivity();
                        }
                    }
                    else {
                        Toast.makeText(this, "Error getting dynamic link", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUser() {
        firestore.collection(Constants.USER).document(mUser.getUid()).get()
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful() && task.getResult() != null) {
                        User user = task.getResult().toObject(User.class);
                        goTOHomeActivity(user);
                    }
                    else {
                        Log.w(TAG, task.getException().getMessage(), task.getException());
                        goToMainActivity();
                    }
                });
    }

    private void goTOHomeActivity(User user) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.USER, user);

        if(recipeID != null && !recipeID.equals("")) {
            intent.putExtra(Constants.RECIPE_ID, recipeID);
        }

        startActivity(intent);
        finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        if(recipeID != null && !recipeID.equals("")) {
            intent.putExtra(Constants.RECIPE_ID, recipeID);
        }

        startActivity(intent);
        finish();
    }
}