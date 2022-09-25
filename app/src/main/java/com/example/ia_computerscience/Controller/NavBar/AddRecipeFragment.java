package com.example.ia_computerscience.Controller.NavBar;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ia_computerscience.Model.FoodType;
import com.example.ia_computerscience.Model.PrivateRecipe;
import com.example.ia_computerscience.Model.PublicRecipe;
import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecipeFragment extends Fragment {
    private static final String TAG = "AddRecipeFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private User user;


    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private DocumentReference documentReference;
    private StorageReference storageReference;

    private Chip private_public;

    private ImageView imageView;
    private Uri imageUri;
    private ActivityResultLauncher<String> activityResultLauncher;

    private EditText txtName;
    private EditText txtIngredients;
    private EditText txtInstructions;

    private ChipGroup chipGroup;
    private Chip[] chips;

    private EditText txtCal;
    private EditText txtTime;
    private Spinner spinner;
    private String selectedTime;

    private Recipe newRecipe;




    public AddRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment AddRecipeFragment.
     */
    public static AddRecipeFragment newInstance(User user) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        private_public = view.findViewById(R.id.AddRecipe_public);
        private_public.setOnClickListener(v -> {
            if(private_public.isChecked()) {
                private_public.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.publicColor)));
                private_public.setChipIconResource(R.drawable.ic_lock_open);
                private_public.setText("Public");
            }
            else {
                private_public.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.privateColor)));
                private_public.setChipIconResource(R.drawable.ic_lock);
                private_public.setText("Private");
            }
        });

        imageView = view.findViewById(R.id.AddRecipe_imageView);
        imageView.setTag(false);
        //selecting image from gallery
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                imageUri -> {
                    if(imageUri == null) { //if image is not selected
                        this.imageUri = null;
                        imageView.setTag(false);
                        imageView.setImageResource(R.drawable.ic_image_add);
                    }
                    else {
                        this.imageUri = imageUri;
                        imageView.setImageURI(imageUri);
                        imageView.setTag(true);
                    }
                });
        imageView.setOnClickListener(v -> activityResultLauncher.launch("image/*"));

        txtName = view.findViewById(R.id.AddRecipe_txtName);
        txtInstructions = view.findViewById(R.id.AddRecipe_txtInstructions);
        txtIngredients = view.findViewById(R.id.AddRecipe_txtIngredients);

        //add on click on button
        view.findViewById(R.id.AddRecipe_btnAdd).setOnClickListener(v -> addNewRecipe());

        setUpChipGroup();

        txtCal = view.findViewById(R.id.AddRecipe_txtCal);
        txtTime = view.findViewById(R.id.AddRecipe_txtTime);

        setUpSpinner();
    }

    public void addNewRecipe() {
        if(!formValid()) {
            return;
        }

        documentReference = firestore.collection(Constants.RECIPE).document();
        String recipeID = documentReference.getId();
        String imageID = recipeID;
        uploadImage(imageID);

        String name = txtName.getText().toString();

        ArrayList<String> ingredients = new ArrayList<>(Arrays.asList(txtIngredients.getText().toString().split("\n")));
        ingredients.removeAll(Arrays.asList(""));
        ArrayList<String> instructions = new ArrayList<>(Arrays.asList(txtInstructions.getText().toString().split("\n")));
        instructions.removeAll(Arrays.asList(""));

        int calories = Integer.parseInt(txtCal.getText().toString());

        double t = Double.parseDouble(txtTime.getText().toString());
        String time;
        if(t%1 == 0) {
            time = (int) t + selectedTime;
        }
        else {
            time = t + selectedTime;
        }

        List<FoodType> foodType = getSelectedFoodType();

        if(private_public.isChecked()) {
            newRecipe = new PublicRecipe(name, recipeID, user.getName(), imageID, ingredients, instructions, calories, time, foodType);
        }
        else {
            newRecipe = new PrivateRecipe(name, recipeID, user.getName(), imageID, ingredients, instructions, calories, time, foodType);
        }

        documentReference.set(newRecipe);
        updateUser(recipeID); //add recipe to list and update database
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ResourceType")
    private boolean formValid() {
        if(txtName.getText().toString().equals("")){
            Toast.makeText(getContext(), "Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if((Boolean) imageView.getTag() == false) {
            Toast.makeText(getContext(), "Image is not selected", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtIngredients.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Ingredients are empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtInstructions.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Instructions are empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtCal.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Calories are empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtTime.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Time is empty", Toast.LENGTH_SHORT).show();
            return false;
        }


        boolean selected = false;
        for(Chip chip : chips) {
            if(chip.isChecked()) {
                selected = true;
            }
        }
        if(!selected) {
            Toast.makeText(getContext(), "Food Type is not selected", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void uploadImage(String path) {
        StorageReference imageRef = storageReference.child(Constants.IMAGE_PATH + path);

        imageRef.putFile(imageUri).addOnCompleteListener(
                task -> {
                    if(!task.isSuccessful()) {
                        task.getException().printStackTrace();
                        Toast.makeText(getContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
                    }
        });
    }

    private void updateUser(String recipeID) {
        user.addRecipeID(recipeID);
        firestore.collection(Constants.USER).document(user.getUserID()).update(Constants.RECIPE_IDS, FieldValue.arrayUnion(recipeID));
    }

    private void setUpChipGroup() {
        chipGroup = getView().findViewById(R.id.AddRecipe_chipGroup);
        FoodType[] f = FoodType.values();
        chips = new Chip[f.length];

        for(int i = 0; i < f.length; i++) {
            Chip chip = new Chip(getContext());
            chip.setText(f[i].getValue());
            chip.setTextSize(16);
            chip.setCheckable(true);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));

            chip.setOnClickListener(view -> {
                if(chip.isChecked()) {
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                    chip.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                    chip.setTextColor(getResources().getColor(R.color.black));
                }
            });

            chipGroup.addView(chip);
            chips[i] = chip;
        }
    }

    private List<FoodType> getSelectedFoodType() {
        List<FoodType> foodType = new ArrayList<>();
        FoodType[] f = FoodType.values();

        for(int i = 0; i < chips.length; i++) {
            if(chips[i].isChecked()) {
                foodType.add(f[i]);
            }
        }

        return foodType;
    }

    private void setUpSpinner() {
        final String[] time = {"min", "hour"};

        spinner = getView().findViewById(R.id.AddRecipe_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, time);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTime = adapterView.getItemAtPosition(i).toString();
//                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}