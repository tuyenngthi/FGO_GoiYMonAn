package com.example.fgo_goiymonan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

    TextView tvSearch;
    TextView tvViewed;
    private TextView tvAccount;
    private EditText edtSearch;
    private RecyclerView rvRecipes;
    private RecipeAdapter recipeAdapter;
    private SpoonacularApiService apiService;
    private final String apiKey = "3c36606ffb104e3283f89d460c802e52";

    private TextView tvNoResult; // thêm dòng này

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tvViewed = findViewById(R.id.tvViewed);
        tvSearch = findViewById(R.id.tvSearch);
        tvAccount = findViewById(R.id.tvAccount);
        edtSearch = findViewById(R.id.edtSearch);
        rvRecipes = findViewById(R.id.rvRecipes);
        tvNoResult = findViewById(R.id.tvNoResult); // ánh xạ

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo adapter trước
        recipeAdapter = new RecipeAdapter(new ArrayList<>());
        rvRecipes.setAdapter(recipeAdapter);

        // Khởi tạo Retrofit + apiService trước
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(SpoonacularApiService.class);

        // Gọi mặc định hiển thị món ăn đầu tiên
        searchRecipes("chicken");

        // Add item click listener for RecyclerView
        recipeAdapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(SearchActivity.this, FoodDetailActivity.class);
            intent.putExtra("recipe", recipe);
            startActivity(intent);
        });

        tvAccount.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AccountActivity.class));
        });

        tvViewed.setOnClickListener(v -> {
            startActivity(new Intent(this, ViewedActivity.class));
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchRecipes(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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
                        recipeAdapter.updateData(new ArrayList<>()); // Xóa dữ liệu cũ
                        return;
                    } else {
                        tvNoResult.setVisibility(View.GONE); // Ẩn nếu có kết quả
                    }

                    // Gọi API lấy nguyên liệu cho từng món
                    for (Recipe recipe : recipes) {
                        fetchRecipeIngredients(recipe);
                    }

                    // Hiển thị danh sách ban đầu (chưa có nguyên liệu)
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

                    int index = recipeAdapter.getRecipeList().indexOf(recipe);
                    Log.d("SearchActivity", "Updating recipe at index: " + index);

                    if (index != -1) {
                        recipeAdapter.notifyItemChanged(index);
                    } else {
                        recipeAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                // Log error nếu cần
                Log.e("SearchActivity", "Failed to load recipe detail", t);
            }
        });
    }

}
