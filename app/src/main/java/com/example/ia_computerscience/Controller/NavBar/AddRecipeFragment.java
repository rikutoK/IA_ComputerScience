package com.example.ia_computerscience.Controller.NavBar;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ia_computerscience.Model.Private_Recipe;
import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    private ImageView imageView;
    private Uri imageUri;
    private ActivityResultLauncher<String> activityResultLauncher;

    private EditText txtName;
    private EditText txtIngredients;
    private EditText txtInstructions;

    private Private_Recipe newRecipe;



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

        List<String> ingredients = Arrays.asList(txtIngredients.getText().toString().split("\n"));
        List<String> instructions = Arrays.asList(txtInstructions.getText().toString().split("\n"));

        newRecipe = new Private_Recipe(name, recipeID, user.getName(), imageID, ingredients, instructions, 0, 30, null);

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
}