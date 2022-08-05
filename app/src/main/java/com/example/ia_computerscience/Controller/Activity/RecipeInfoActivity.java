package com.example.ia_computerscience.Controller.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RecipeInfoActivity extends AppCompatActivity {
    private static final String TAG = "RecipeInfoActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private StorageReference storageReference;
    final private long FIVE_MEGABYTE = 1024 * 1024 * 5;

    private Recipe recipe;

    private TextView txtName;
    private TextView txtLike;
    private TextView txtCal;
    private TextView txtTime;
    private TextView txtShare;
    private ImageView imageView;
    private TextView txtIngredients;
    private TextView txtInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        if(!getIntent().hasExtra(Constants.RECIPE)) {
            Toast.makeText(this, "Error: Could not get recipe", Toast.LENGTH_LONG).show();
            return;
        }

        recipe = (Recipe) getIntent().getSerializableExtra(Constants.RECIPE);

        getImage();

        txtName = findViewById(R.id.RecipeInfo_txtName);
        txtLike = findViewById(R.id.RecipeInfo_txtLike);
        txtCal = findViewById(R.id.RecipeInfo_txtCal);
        txtTime = findViewById(R.id.RecipeInfo_txtTime);
        txtShare = findViewById(R.id.Recipeinfo_txtShare);
        imageView = findViewById(R.id.RecipeInfo_imageView);
        txtIngredients = findViewById(R.id.RecipeInfo_txtIngredients);
        txtInstructions = findViewById(R.id.RecipeInfo_txtInstructions);

        txtName.setText(recipe.getName());
//
        txtCal.setText(recipe.getCalories() + "kcl");
        txtTime.setText(recipe.getTime());
//
        String ingredients = "Ingredients \n \n";
        for(String s : recipe.getIngredients()) {
            ingredients += s + "\n";
        }
        txtIngredients.setText(ingredients.substring(0, ingredients.length() - 2));

        String instructions = "Steps \n \n";
        for(String s : recipe.getSteps()) {
            instructions += s + "\n";
        }
        txtInstructions.setText(instructions.substring(0, instructions.length() - 2));
    }

    private void getImage() {
        storageReference = FirebaseStorage.getInstance().getReference(Constants.IMAGE_PATH + recipe.getImageID());
        storageReference.getBytes(FIVE_MEGABYTE)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        imageView.setImageBitmap(bitmap);
                        imageView.getLayoutParams().width = width;
                        imageView.getLayoutParams().height = height;
                    }
                });
    }
}