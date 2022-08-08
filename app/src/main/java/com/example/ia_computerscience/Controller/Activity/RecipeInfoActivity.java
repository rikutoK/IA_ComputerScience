package com.example.ia_computerscience.Controller.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ia_computerscience.Model.FoodType;
import com.example.ia_computerscience.Model.Private_Recipe;
import com.example.ia_computerscience.Model.Public_Recipe;
import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RecipeInfoActivity extends AppCompatActivity {
    private static final String TAG = "RecipeInfoActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private StorageReference storageReference;
    final private long FIVE_MEGABYTE = 1024 * 1024 * 5;

    private Recipe recipe;

    private User user;

    private TextView txtName;
    private TextView txtLikes;
    private TextView txtCal;
    private TextView txtTime;
    private TextView txtShare;

    private ChipGroup chipGroup;

    private ImageView imageView;
    private TextView txtAuthor;
    private TextView txtIngredients;
    private TextView txtInstructions;

    private Button btnRemove;

    private boolean liked;
    private boolean initially_liked;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info);

        firestore = FirebaseFirestore.getInstance();

        if(!getIntent().hasExtra(Constants.RECIPE)) {
            Toast.makeText(this, "Error: Could not get recipe", Toast.LENGTH_LONG).show();
            return;
        }
        if(!getIntent().hasExtra(Constants.USER)) {
            Toast.makeText(this, "Error: Could not get user", Toast.LENGTH_LONG).show();
            return;
        }

        recipe = (Recipe) getIntent().getSerializableExtra(Constants.RECIPE);
        user = (User) getIntent().getSerializableExtra(Constants.USER);

        getImage();

        txtName = findViewById(R.id.RecipeInfo_txtName);
        txtLikes = findViewById(R.id.RecipeInfo_txtLikes);
        txtCal = findViewById(R.id.RecipeInfo_txtCal);
        txtTime = findViewById(R.id.RecipeInfo_txtTime);
        txtShare = findViewById(R.id.Recipeinfo_txtShare);

        chipGroup = findViewById(R.id.RecipeInfo_chipGroup);

        imageView = findViewById(R.id.RecipeInfo_imageView);
        txtAuthor = findViewById(R.id.RecipeInfo_txtAuthor);
        txtIngredients = findViewById(R.id.RecipeInfo_txtIngredients);
        txtInstructions = findViewById(R.id.RecipeInfo_txtInstructions);

        btnRemove = findViewById(R.id.RecipeInfo_btnRemove);

        intent = new Intent();


        txtName.setText(recipe.getName());

        LinearLayout linearLayout = findViewById(R.id.RecipeInfo_linearLayout);
        if(recipe instanceof Private_Recipe) {
            linearLayout.removeView(txtLikes);
        }
        else {
            linearLayout.removeView(btnRemove);

            txtLikes.setText(((Public_Recipe)recipe).getLikes() + "");

            liked = false;
            initially_liked = false;
            if(user.getRecipeIDs().contains(recipe.getRecipeID())) {
                liked = true;
                initially_liked = true;
                txtLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
            }
        }


        txtCal.setText(recipe.getCalories() + "kcl");
        txtTime.setText(recipe.getTime());

        //setting up chip group
        for(FoodType f : recipe.getFoodType()) {
            Chip chip = new Chip(this);
            chip.setText(f.getValue());
            chip.setTextSize(16);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
            chip.setTextColor(getResources().getColor(R.color.white));

            chipGroup.addView(chip);
        }

        txtAuthor.setText("Created by " + recipe.getAuthor());

        String ingredients = "Ingredients \n \n";
        for(String s : recipe.getIngredients()) {
            ingredients += s + "\n";
        }
        txtIngredients.setText(ingredients.substring(0, ingredients.length() - 2));

        String instructions = "Steps \n \n";
        for(String s : recipe.getSteps()) {
            instructions += s + "\n \n";
        }
        txtInstructions.setText(instructions.substring(0, instructions.length() - 5));
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

    public void removeRecipe(View view) {
        intent = new Intent();
        intent.putExtra(Constants.RECIPE_ID, recipe.getRecipeID());
        intent.putExtra(Constants.REMOVE, true);
        setResult(RESULT_OK, intent);

        Toast.makeText(this, "Recipe removed successfully", Toast.LENGTH_SHORT).show();

        finish();
    }



    public void like(View veiw) {
        int likes = Integer.parseInt(txtLikes.getText().toString());

        if(liked) {
            liked = false;

            txtLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
            txtLikes.setText(--likes + "");

            if(initially_liked) {
                intent.putExtra(Constants.RECIPE_ID, recipe.getRecipeID());
                intent.putExtra(Constants.REMOVE, true);
            }
            else {
                intent = new Intent();
            }

            setResult(RESULT_OK, intent);

            Toast.makeText(this, "Recipe removed", Toast.LENGTH_SHORT).show();

        }
        else {
            liked = true;

            txtLikes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
            txtLikes.setText(++likes + "");

            if(initially_liked) {
                intent = new Intent();
            }
            else {
                intent.putExtra(Constants.RECIPE_ID, recipe.getRecipeID());
                intent.putExtra(Constants.REMOVE, true);
            }

            setResult(RESULT_OK, intent);

            Toast.makeText(this, "Recipe saved", Toast.LENGTH_SHORT).show();
        }
    }


}