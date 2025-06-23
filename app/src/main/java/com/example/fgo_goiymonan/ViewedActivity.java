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

public class ViewedActivity extends AppCompatActivity {

    private RecyclerView rvViewed;
    private RecipeAdapter recipeAdapter;
    private TextView tvNoResult;
    private TextView tvSearch, tvFavorite, tvAccount;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewed);

        rvViewed = findViewById(R.id.rvRecipes);
        tvNoResult = findViewById(R.id.tvNoResult);
        tvSearch = findViewById(R.id.tvSearch);
        tvFavorite = findViewById(R.id.tvFavorite);
        tvAccount = findViewById(R.id.tvAccount);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvViewed.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(new ArrayList<>());
        rvViewed.setAdapter(recipeAdapter);

        loadViewed();

        recipeAdapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(ViewedActivity.this, FoodDetailActivity.class);
            intent.putExtra("id", recipe.getId());
            intent.putExtra("title", recipe.getTitle());
            intent.putExtra("image", recipe.getImage());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("readyInMinutes", recipe.getReadyInMinutes());
            intent.putExtra("instructions", recipe.getInstructions());
            startActivity(intent);
        });

        tvSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        tvFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));
        tvAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
    }

    private void loadViewed() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("viewed")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Recipe> viewedList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Recipe recipe = new Recipe(
                                    doc.getLong("id").intValue(),
                                    doc.getString("title"),
                                    doc.getString("image")
                            );
                            recipe.setIngredients(doc.getString("ingredients"));

                            Long readyInMinutesLong = doc.getLong("readyInMinutes");
                            recipe.setReadyInMinutes(readyInMinutesLong != null ? readyInMinutesLong.intValue() : 0);

                            String instructions = doc.getString("instructions");
                            recipe.setInstructions(instructions != null ? instructions : "");

                            viewedList.add(recipe);
                        }
                        if (viewedList.isEmpty()) {
                            tvNoResult.setVisibility(View.VISIBLE);
                        } else {
                            tvNoResult.setVisibility(View.GONE);
                        }
                        recipeAdapter.updateData(viewedList);
                    } else {
                        Toast.makeText(this, "Failed to load viewed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
