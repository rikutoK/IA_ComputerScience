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
import com.example.ia_computerscience.Model.PublicRecipe;
import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublicRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicRecipeFragment extends Fragment implements RecViewAdapter.OnViewClickListner{
    private static final String TAG = "PublicRecipeFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private User user;
    private ArrayList<Recipe> recipeList;
    private RecViewAdapter adapter;
    private RecyclerView recView;
    private TextView txtEmpty;

    private FirebaseFirestore firestore;

    private SearchView searchView;

    private Spinner spinner;
    private Chip chip;
    private boolean first;
    private String selectedRole;
    private Map<String, String> key;

    private Query nextPage;
    private final int LIMIT = 3;
    private boolean pageEnd;

    private boolean loading;

    private ChipGroup chipGroup;
    private Chip[] chips;

    private ActivityResultLauncher<Intent> startForResult;


    public PublicRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user Parameter 1.
     * @return A new instance of fragment PublicRecipeFragment.
     */
    public static PublicRecipeFragment newInstance(User user) {
        PublicRecipeFragment fragment = new PublicRecipeFragment();
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
        return inflater.inflate(R.layout.fragment_public_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();

        recView = view.findViewById(R.id.PublicRecipe_recView);
        txtEmpty = view.findViewById(R.id.PublicRecipe_txtEmpty);

        first = true;
        setUpSpinner(); //calls getRecipe and sets up rec view

        searchView = view.findViewById(R.id.PublicRecipe_searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        setUpChipGroup();

        startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null && result.getData().getStringExtra(Constants.RECIPE_ID) != null && result.getData().hasExtra(Constants.REMOVE)) {
                    String recipeID = result.getData().getStringExtra(Constants.RECIPE_ID);

                    if (result.getData().getExtras().getBoolean(Constants.REMOVE)) { //remove
                        firestore.collection(Constants.USER).document(user.getUserID()).update(Constants.RECIPE_IDS, FieldValue.arrayRemove(recipeID));
                        firestore.collection(Constants.RECIPE).document(recipeID).update(Constants.LIKES, FieldValue.increment(-1));
                        user.getRecipeIDs().remove(recipeID);
                    }
                    else {
                        firestore.collection(Constants.USER).document(user.getUserID()).update(Constants.RECIPE_IDS, FieldValue.arrayUnion(recipeID));
                        firestore.collection(Constants.RECIPE).document(recipeID).update(Constants.LIKES, FieldValue.increment(1));
                        user.getRecipeIDs().add(recipeID);
                    }
                }
            }
        });
    }

    private void getRecipe(Query query, boolean newPage) {
        if(newPage) {
            recipeList = new ArrayList<>();
            pageEnd = false;
        }

        if(pageEnd || loading) {
            return;
        }

        loading = true;
        query.limit(LIMIT).get().addOnCompleteListener(getActivity(), task -> {
                loading = false;

                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        recipeList.add(document.toObject(PublicRecipe.class));
                    }

                    if(task.getResult().size() < LIMIT) { //check if next page exists
                        pageEnd = true;
                    }
                    else {
                        DocumentSnapshot last = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        nextPage = query.startAfter(last); //storing next page
                    }

                    if(first) {
                        setUpRecView();
                        first = false;
                    }
                    else {
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
                }
                else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    Log.w(TAG, task.getException().getMessage(), task.getException());
                }
        });
    }

    private void search(String search) {
        if(search.equals("")) {
            getRecipe(getFilter(), true);
            return;
        }

        Query query = firestore.collection(Constants.RECIPE).whereEqualTo(Constants.PUBLIC, true).orderBy(Constants.NAME).whereGreaterThan(Constants.NAME, search).whereLessThan(Constants.NAME, search + "\uf8ff");
        getRecipe(query, true);
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

        recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!recyclerView.canScrollVertically(1)) {
                    getRecipe(nextPage, false); //loads next page of recipe with a specified query
                }
            }
        });
    }

    private void setUpSpinner() {
        final String[] sort = {"Likes", "Name", "Cal", "Time"};
        key = new HashMap<>();
        key.put(sort[0], Constants.LIKES);
        key.put(sort[1], Constants.NAME);
        key.put(sort[2], Constants.CALORIES);
        key.put(sort[3], Constants.TIME);

        spinner = getView().findViewById(R.id.PublicRecipe_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sort);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRole = adapterView.getItemAtPosition(i).toString();
                getRecipe(getFilter(), true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        chip = getView().findViewById(R.id.PublicRecipe_chip);
        chip.setOnClickListener(v -> {
            getRecipe(getFilter(), true);

            if(chip.isChecked()) {
                chip.setChipIconResource(R.drawable.ic_up);
            }
            else {
                chip.setChipIconResource(R.drawable.ic_down);
            }
        });
    }

    private void setUpChipGroup() {
        chipGroup = getView().findViewById(R.id.PublicRecipe_chipGroup);
        FoodType[] f = FoodType.values();
        chips = new Chip[f.length];

        for(int i = 0; i < f.length; i++) {
            Chip chip = new Chip(getContext());
            chip.setText(f[i].getValue());
            chip.setTextSize(16);
            chip.setCheckable(true);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));

            chip.setOnClickListener(view -> {
                getRecipe(getFilter(), true);

                if(chip.isChecked()) {
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_200)));
                    chip.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                    chip.setTextColor(getResources().getColor(R.color.black));
                }
            });

            chip.setOnCheckedChangeListener((compoundButton, b) -> {
                chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                chip.setTextColor(getResources().getColor(R.color.black));
            });

            chipGroup.addView(chip);
            chips[i] = chip;
        }
    }

    private Query getFilter() {
        Query query;

        if(chip.isChecked()) {
            if(selectedRole.equals("Likes")) {
                query = firestore.collection(Constants.RECIPE).whereEqualTo(Constants.PUBLIC, true).orderBy(key.get(selectedRole), Query.Direction.ASCENDING);
            }
            else {
                query = firestore.collection(Constants.RECIPE).whereEqualTo(Constants.PUBLIC, true).orderBy(key.get(selectedRole), Query.Direction.DESCENDING);
            }
        }
        else {
            if(selectedRole.equals("Likes")) {
                query = firestore.collection(Constants.RECIPE).whereEqualTo(Constants.PUBLIC, true).orderBy(key.get(selectedRole), Query.Direction.DESCENDING);
            }
            else {
                query = firestore.collection(Constants.RECIPE).whereEqualTo(Constants.PUBLIC, true).orderBy(key.get(selectedRole), Query.Direction.ASCENDING);
            }
        }

        return filterFoodType(query);
    }

    private Query filterFoodType(Query query) {
        FoodType[] f = FoodType.values();

        for(int i = 0; i < chips.length; i++) {
            if(chips[i].isChecked()) {
                query = query.whereArrayContains(Constants.FOODTYPE, f[i]);
            }
        }

        return query;
    }

    @Override
    public void onViewClick(int position) {
        Recipe recipe = recipeList.get(position);
        Intent intent = new Intent(getContext(), RecipeInfoActivity.class);
        intent.putExtra(Constants.RECIPE, recipe);
        intent.putExtra(Constants.USER, user);
        intent.putExtra(Constants.PUBLIC, true);
        startForResult.launch(intent);
    }
}