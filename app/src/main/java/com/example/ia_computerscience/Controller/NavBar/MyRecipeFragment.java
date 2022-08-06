package com.example.ia_computerscience.Controller.NavBar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

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
import android.widget.Toast;

import com.example.ia_computerscience.Controller.Activity.RecipeInfoActivity;
import com.example.ia_computerscience.Controller.RecView.RecViewAdapter;
import com.example.ia_computerscience.Model.Private_Recipe;
import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
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
    private ArrayList<Recipe> entireRecipeList;
    private int index;

    private SearchView searchView;

    private Spinner spinner;
    private Chip chip;

    private ArrayList<Recipe> recipeList;

    private RecViewAdapter adapter;

    private FirebaseFirestore firestore;


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

        searchView = view.findViewById(R.id.MyRecipe_searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        getRecipes(); //gets reciep, set up rec view, set up spinner
    }

    private void filterList(String newText) {
        recipeList = new ArrayList<>();

        for(Recipe recipe : entireRecipeList) {
            if(recipe.getName().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))) {
                recipeList.add(recipe);
            }
        }

        adapter.setRecipeList(recipeList);
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
                switch(adapterView.getItemAtPosition(i).toString()) {
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

    private void getRecipes() {
        index = 0;
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
                                Private_Recipe recipe = document.toObject(Private_Recipe.class);
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
                           Private_Recipe recipe = document.toObject(Private_Recipe.class);
                           entireRecipeList.add(recipe);
                       }
                       recipeList = entireRecipeList;

                       setUpRecView(); //sets up recyclerview

                       setUpSpinner(); //sets up spinner
                   }
                   else {
                       Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                   }
                });
    }

    private void setUpRecView() {
        RecyclerView recView = getView().findViewById(R.id.MyRecipe_recView);
        adapter = new RecViewAdapter(getContext(), recipeList, this);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onViewClick(int position) {
        Recipe recipe = recipeList.get(position);
        Intent intent = new Intent(getContext(), RecipeInfoActivity.class);
        intent.putExtra(Constants.RECIPE, recipe);
        startActivity(intent);
    }
}