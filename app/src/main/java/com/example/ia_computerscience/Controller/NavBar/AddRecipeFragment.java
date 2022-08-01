package com.example.ia_computerscience.Controller.NavBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ia_computerscience.Model.FoodType;
import com.example.ia_computerscience.Model.Private_Recipe;
import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private ImageView image;
    private ActivityResultLauncher<String> activityResultLauncher;

    private EditText txtName;
    private EditText txtIngredients;
    private EditText txtInstructions;

    private Recipe newRecipe;



    public AddRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRecipeFragment newInstance(String param1, String param2) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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

        storageReference = FirebaseStorage.getInstance().getReference();

        image = view.findViewById(R.id.AddRecipe_imageView);
        //selecting image from gallery
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                imageUri -> {
                    image.setImageURI(imageUri);
                    image.setTag(imageUri);
                });
        image.setOnClickListener(v -> activityResultLauncher.launch("image/*"));

        txtName = view.findViewById(R.id.AddRecipe_txtName);
        txtInstructions = view.findViewById(R.id.AddRecipe_txtInstructions);
        txtIngredients = view.findViewById(R.id.AddRecipe_txtIngredients);

        //add on click on button
        Button btnAdd = view.findViewById(R.id.AddRecipe_btnAdd);
        btnAdd.setOnClickListener(v -> addNewRecipe());
    }

    public void addNewRecipe() {
        if(!formValid()) {
            return;
        }

        String imageID = uploadImage();
        String name = txtName.getText().toString();
        ArrayList<String> ingredients = (ArrayList<String>) Arrays.asList(txtIngredients.getText().toString().split("\n"));
        ArrayList<String> instructions = (ArrayList<String>) Arrays.asList(txtInstructions.getText().toString().split("\n"));

        newRecipe = new Private_Recipe();
    }

    @SuppressLint("ResourceType")
    private boolean formValid() {
        if(txtName.getText().toString().equals("")){
            Toast.makeText(getContext(), "Name is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(image.getTag() == null || (int) image.getTag() == R.drawable.ic_image) {
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

        return true;
    }

    private String uploadImage() {


        return null;
    }
}