package com.example.ia_computerscience.Controller.NavBar;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.ia_computerscience.Controller.Activity.RecipeInfoActivity;
import com.example.ia_computerscience.Controller.RecView.RecViewAdapter;
import com.example.ia_computerscience.Model.Private_Recipe;
import com.example.ia_computerscience.Model.Recipe;
import com.example.ia_computerscience.Model.User;
import com.example.ia_computerscience.R;
import com.example.ia_computerscience.Util.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
    private ArrayList<Recipe> recipeList;

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

        getRecipes();
    }

    private void getRecipes() {
        recipeList = new ArrayList<>();

        if(user.getRecipeIDs().size() == 0) {
            return;
        }

        firestore.collection(Constants.RECIPE).whereIn(Constants.RECIPE_ID, user.getRecipeIDs()).get()
                .addOnCompleteListener(getActivity(), task -> {
                   if(task.isSuccessful()) {
                       for(QueryDocumentSnapshot document : task.getResult()) {
                           Private_Recipe recipe = document.toObject(Private_Recipe.class);
                           recipeList.add(recipe);
                       }
                       setUpRecView(); //sets up recyclerview
                   }
                   else {
                       Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                   }
                });
    }

    private void setUpRecView() {
        RecyclerView recView = getView().findViewById(R.id.MyRecipe_recView);
        RecViewAdapter adapter = new RecViewAdapter(getContext(), recipeList, this);
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onViewClick(int position) {
        firestore.collection(Constants.RECIPE).document(user.getRecipeIDs().get(position)).get()
                        .addOnCompleteListener(getActivity(), task -> {
                            if(task.isSuccessful() && task.getResult() != null) {
                                Recipe recipe = task.getResult().toObject(Private_Recipe.class);
                                Intent intent = new Intent(getContext(), RecipeInfoActivity.class);
                                intent.putExtra(Constants.RECIPE, recipe);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
    }
}