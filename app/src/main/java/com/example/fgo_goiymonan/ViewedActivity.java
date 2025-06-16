package com.example.fgo_goiymonan;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import java.util.List;

public class ViewedActivity extends AppCompatActivity {
    private TextView tvSearch;
    private RecyclerView rvRecipes;
    private TextView tvNoResult;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewed);

        tvSearch = findViewById(R.id.tvSearch);
        rvRecipes = findViewById(R.id.rvRecipes);
        tvNoResult = findViewById(R.id.tvNoResult);

        tvSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(ViewedStorage.getViewedList(this));
        rvRecipes.setAdapter(recipeAdapter);

        recipeAdapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(ViewedActivity.this, FoodDetailActivity.class);
            intent.putExtra("recipe", recipe);
            startActivity(intent);
        });

        updateUI();
    }

    private void updateUI() {
        if (recipeAdapter.getRecipeList().isEmpty()) {
            tvNoResult.setVisibility(View.VISIBLE);
        } else {
            tvNoResult.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        recipeAdapter.updateData(ViewedStorage.getViewedList(this));
        updateUI();
    }
}
