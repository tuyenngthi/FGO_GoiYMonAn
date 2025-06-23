package com.example.fgo_goiymonan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private RecipeAdapter recipeAdapter;
    private TextView tvNoResult;
    private TextView tvSearch, tvViewed, tvAccount;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        rvFavorites = findViewById(R.id.rvRecipes);
        tvNoResult = findViewById(R.id.tvNoResult);
        tvSearch = findViewById(R.id.tvSearch);
        tvViewed = findViewById(R.id.tvViewed);
        tvAccount = findViewById(R.id.tvAccount);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(new ArrayList<>());
        rvFavorites.setAdapter(recipeAdapter);

        loadFavorites();

        recipeAdapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(FavoriteActivity.this, FoodDetailActivity.class);
            intent.putExtra("id", recipe.getId());
            intent.putExtra("title", recipe.getTitle());
            intent.putExtra("image", recipe.getImage());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("readyInMinutes", recipe.getReadyInMinutes());
            intent.putExtra("instructions", recipe.getInstructions());
            startActivity(intent);
        });

        // Xử lý chuyển tab:
        tvSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        tvViewed.setOnClickListener(v -> startActivity(new Intent(this, ViewedActivity.class)));
        tvAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
    }

    private void loadFavorites() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Recipe> favorites = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Recipe recipe = new Recipe(
                                    doc.getLong("id").intValue(),
                                    doc.getString("title"),
                                    doc.getString("image")
                            );
                            recipe.setIngredients(doc.getString("ingredients"));
                            recipe.setReadyInMinutes(doc.getLong("readyInMinutes").intValue());
                            recipe.setInstructions(doc.getString("instructions"));
                            favorites.add(recipe);
                        }
                        if (favorites.isEmpty()) {
                            tvNoResult.setVisibility(View.VISIBLE);
                        } else {
                            tvNoResult.setVisibility(View.GONE);
                        }
                        recipeAdapter.updateData(favorites);
                    } else {
                        Toast.makeText(this, "Failed to load favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
