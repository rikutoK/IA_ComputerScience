package com.example.ia_computerscience.Controller.NavBar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ia_computerscience.Controller.Activity.RecipeInfoActivity;
import com.example.ia_computerscience.Controller.RecView.RecViewAdapter;
import com.example.ia_computerscience.Model.FoodType;
import com.example.ia_computerscience.Model.Private_Recipe;
import com.example.ia_computerscience.Model.Public_Recipe;
import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyRecipeFragment extends Fragment implements RecViewAdapter.OnViewClickListner {
    private static final String TAG = "MyRecipeFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private User user;
    private ArrayList<Recipe> entireRecipeList; //stores all recipe in the list
    private ArrayList<Recipe> recipeList; //recipe list on display

    private RecViewAdapter adapter;
    private RecyclerView recView;
    private TextView txtEmpty;

    private FirebaseFirestore firestore;

    private SearchView searchView;

    private Spinner spinner;
    private Chip chip;
    private String selectedRole;

    private ChipGroup chipGroup;
    private Chip[] chips;

    private ActivityResultLauncher<Intent> startForResult;


    public MyRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment MyRecipeFragment.
     */
    public static MyRecipeFragment newInstance(User user) {
        MyRecipeFragment fragment = new MyRecipeFragment();
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
        return inflater.inflate(R.layout.fragment_my_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();

        recView = view.findViewById(R.id.MyRecipe_recView);
        txtEmpty = view.findViewById(R.id.MyRecipe_txtEmpty);

        getRecipes(); //gets reciep, set up rec view, set up spinner

        searchView = view.findViewById(R.id.MyRecipe_searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });

        setUpChipGroup();

        //check if the recipe was removed in the RecipeInfoActivity
        startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getData() != null && result.getData().getStringExtra(Constants.RECIPE_ID) != null && result.getData().hasExtra(Constants.REMOVE)) {
                    String recipeID = result.getData().getStringExtra(Constants.RECIPE_ID);

                    if(result.getData().getExtras().getBoolean(Constants.REMOVE)) { //remove
                        user.getRecipeIDs().remove(recipeID);

                        Recipe recipe = null;

                        for(Recipe r : entireRecipeList) {
                            if (r.getRecipeID().equals(recipeID)) {
                                recipe = r;
                            }
                        }

                        entireRecipeList.remove(recipe);
                        recipeList.remove(recipe);
                        adapter.setRecipeList(recipeList);

                        //remove from firebase
                        firestore.collection(Constants.USER).document(user.getUserID()).update(Constants.RECIPE_IDS, FieldValue.arrayRemove(recipe.getRecipeID()));
                        user.getRecipeIDs().remove(recipe.getRecipeID());

                        if(recipe instanceof Public_Recipe) {
                            Public_Recipe pRecipe = (Public_Recipe) recipe;
                            firestore.collection(Constants.RECIPE).document(pRecipe.getRecipeID()).update(Constants.LIKES, FieldValue.increment(-1));
                            pRecipe.setLikes(pRecipe.getLikes() - 1);
                        }
                    }
                    else { //add
                        user.getRecipeIDs().add(recipeID);

                        Recipe recipe = null;

                        for(Recipe r : entireRecipeList) {
                            if (r.getRecipeID().equals(recipeID)) {
                                recipe = r;
                            }
                        }

                        entireRecipeList.add(recipe);
                        recipeList.add(recipe);
                        adapter.setRecipeList(recipeList);

                        //add to firebase
                        firestore.collection(Constants.USER).document(user.getUserID()).update(Constants.RECIPE_IDS, FieldValue.arrayUnion(recipe.getRecipeID()));
                        user.getRecipeIDs().add(recipe.getRecipeID());

                        if(recipe instanceof Public_Recipe) {
                            Public_Recipe pRecipe = (Public_Recipe) recipe;
                            firestore.collection(Constants.RECIPE).document(pRecipe.getRecipeID()).update(Constants.LIKES, FieldValue.increment(1));
                            pRecipe.setLikes(pRecipe.getLikes() + 1);
                        }
                    }
                }
            }
        });
    }

    private void search(String newText) {
        recipeList = new ArrayList<>();

        for(Recipe recipe : entireRecipeList) {
            if(recipe.getName().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))) {
                recipeList.add(recipe);
            }
        }

        for(int i = recipeList.size() - 1; i >= 0; i--) {
            for (FoodType f : getSelectedFoodType()) {
                if(!recipeList.get(i).getFoodType().contains(f)) {
                    recipeList.remove(i);
                }
            }
        }

        sort();

        adapter.setRecipeList(recipeList);

        if(recipeList.size() == 0) {
            txtEmpty.setVisibility(View.VISIBLE);
            recView.setVisibility(View.INVISIBLE);
        }
        else {
            txtEmpty.setVisibility(View.INVISIBLE);
            recView.setVisibility(View.VISIBLE);
        }
    }

    private void setUpSpinner() {
        final String[] sort = {"Name", "Cal", "Time"};

        spinner = getView().findViewById(R.id.MyRecipe_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sort);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRole = adapterView.getItemAtPosition(i).toString();
                sort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        chip = getView().findViewById(R.id.MyRecipe_chip);
        chip.setOnClickListener(v -> {
            Collections.reverse(recipeList);
            adapter.setRecipeList(recipeList);

            if(chip.isChecked()) {
                chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                chip.setChipIconResource(R.drawable.ic_up);
            }
            else {
                chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                chip.setChipIconResource(R.drawable.ic_down);
            }
        });
    }

    private void sort() {
        switch(selectedRole) {
            case "Name":
                Collections.sort(recipeList, Recipe :: compareNameTo);
                adapter.setRecipeList(recipeList);
                break;
            case "Cal":
                Collections.sort(recipeList, Recipe :: compareCalTo);
                adapter.setRecipeList(recipeList);
                break;
            case "Time":
                Collections.sort(recipeList, Recipe :: compareTimeTo);
                adapter.setRecipeList(recipeList);
                break;
        }

        if(chip.isChecked()) {
            Collections.reverse(recipeList);
        }
    }

    private void getRecipes() {
        int index = 0;
        entireRecipeList = new ArrayList<>();
        recipeList = new ArrayList<>();

        if(user.getRecipeIDs().size() == 0) {
            return;
        }

        ArrayList<String> recipeIDs = new ArrayList<>();

        while(index + 10 < user.getRecipeIDs().size()) {
            recipeIDs.clear();
            for(int i = 0; i < 10; i++) {
                recipeIDs.add(user.getRecipeIDs().get(index));
                index++;
            }

            firestore.collection(Constants.RECIPE).whereIn(Constants.RECIPE_ID, recipeIDs).get()
                    .addOnCompleteListener(getActivity(), task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe;
                                if(document.contains(Constants.LIKES)) {
                                    recipe = document.toObject(Public_Recipe.class);
                                }
                                else {
                                    recipe = document.toObject(Private_Recipe.class);
                                }

                                entireRecipeList.add(recipe);
                            }
                        }
                        else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

        recipeIDs.clear();
        for(int i = index; i < user.getRecipeIDs().size(); i++) {
            recipeIDs.add(user.getRecipeIDs().get(i));
        }

        firestore.collection(Constants.RECIPE).whereIn(Constants.RECIPE_ID, recipeIDs).get()
                .addOnCompleteListener(getActivity(), task -> {
                   if(task.isSuccessful()) {
                       for(QueryDocumentSnapshot document : task.getResult()) {
                           Recipe recipe;

                           if(document.contains(Constants.LIKES)) {
                               recipe = document.toObject(Public_Recipe.class);
                           }
                           else {
                               recipe = document.toObject(Private_Recipe.class);
                           }

                           entireRecipeList.add(recipe);
                       }
                       recipeList = (ArrayList<Recipe>) entireRecipeList.clone();

                       setUpRecView(); //sets up recyclerview

                       setUpSpinner(); //sets up spinner
                   }
                   else {
                       Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                   }
                });
    }

    private void setUpRecView() {
        adapter = new RecViewAdapter(getContext(), recipeList, this);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(recipeList.size() == 0) {
            txtEmpty.setVisibility(View.VISIBLE);
            recView.setVisibility(View.INVISIBLE);
        }
        else {
            txtEmpty.setVisibility(View.INVISIBLE);
            recView.setVisibility(View.VISIBLE);
        }
    }

    private void setUpChipGroup() {
        chipGroup = getView().findViewById(R.id.MyRecipe_chipGroup);
        FoodType[] f = FoodType.values();
        chips = new Chip[f.length];

        for(int i = 0; i < f.length; i++) {
            Chip chip = new Chip(getContext());
            chip.setText(f[i].getValue());
            chip.setTextSize(16);
            chip.setCheckable(true);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));

            FoodType foodType = f[i];

            chip.setOnClickListener(view -> {
                if(chip.isChecked()) {
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                    chip.setTextColor(getResources().getColor(R.color.white));

                    for(int j = recipeList.size() - 1; j >= 0; j--) {
                        if(!recipeList.get(j).getFoodType().contains(foodType)) {
                            recipeList.remove(j);
                        }
                    }
                    adapter.setRecipeList(recipeList);
                }
                else {
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                    chip.setTextColor(getResources().getColor(R.color.black));

                    search(String.valueOf(searchView.getQuery()));
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

    @Override
    public void onViewClick(int position) {
        Recipe recipe = recipeList.get(position);
        Intent intent = new Intent(getContext(), RecipeInfoActivity.class);
        intent.putExtra(Constants.RECIPE, recipe);
        intent.putExtra(Constants.USER, user);
        startForResult.launch(intent);
    }
}