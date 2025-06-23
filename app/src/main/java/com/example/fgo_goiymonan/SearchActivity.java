package com.example.fgo_goiymonan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;
    private RecyclerView rvRecipes;
    private RecipeAdapter recipeAdapter;
    private SpoonacularApiService apiService;
    private final String apiKey = "b73324a30437465c891f07f9c5dea8e6";
    private TextView tvNoResult;

    private TextView tvFavorite, tvViewed, tvAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch = findViewById(R.id.edtSearch);
        rvRecipes = findViewById(R.id.rvRecipes);
        tvNoResult = findViewById(R.id.tvNoResult);
        tvFavorite = findViewById(R.id.tvFavorite);
        tvViewed = findViewById(R.id.tvViewed);
        tvAccount = findViewById(R.id.tvAccount);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        recipeAdapter = new RecipeAdapter(new ArrayList<>());
        rvRecipes.setAdapter(recipeAdapter);

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(SpoonacularApiService.class);
        searchRecipes("chicken");

        recipeAdapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(SearchActivity.this, FoodDetailActivity.class);
            intent.putExtra("id", recipe.getId());
            intent.putExtra("title", recipe.getTitle());
            intent.putExtra("image", recipe.getImage());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("readyInMinutes", recipe.getReadyInMinutes());
            intent.putExtra("instructions", recipe.getInstructions());
            startActivity(intent);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchRecipes(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // XỬ LÝ CHUYỂN TAB
        tvFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));
        tvViewed.setOnClickListener(v -> startActivity(new Intent(this, ViewedActivity.class)));
        tvAccount.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
    }

    private void searchRecipes(String query) {
        Call<RecipeResponse> call = apiService.searchRecipes(query, 10, apiKey);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = response.body().getResults();
                    if (recipes.isEmpty()) {
                        tvNoResult.setVisibility(View.VISIBLE);
                        recipeAdapter.updateData(new ArrayList<>());
                        return;
                    } else {
                        tvNoResult.setVisibility(View.GONE);
                    }
                    for (Recipe recipe : recipes) {
                        fetchRecipeIngredients(recipe);
                    }
                    recipeAdapter.updateData(recipes);
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRecipeIngredients(Recipe recipe) {
        Call<RecipeDetail> detailCall = apiService.getRecipeInformation(recipe.getId(), apiKey);
        detailCall.enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RecipeDetail.ExtendedIngredient> ingredientsList = response.body().getExtendedIngredients();
                    StringBuilder ingredientsText = new StringBuilder();
                    for (int i = 0; i < Math.min(3, ingredientsList.size()); i++) {
                        ingredientsText.append(ingredientsList.get(i).getOriginal());
                        if (i < Math.min(3, ingredientsList.size()) - 1) {
                            ingredientsText.append(", ");
                        }
                    }
                    recipe.setIngredients(ingredientsText.toString().trim());
                    recipe.setReadyInMinutes(response.body().getReadyInMinutes());
                    recipe.setInstructions(response.body().getInstructions() != null ? response.body().getInstructions() : "Instructions not available.");
                    recipeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {}
        });
    }
}
