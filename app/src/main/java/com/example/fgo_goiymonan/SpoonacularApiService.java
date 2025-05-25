package com.example.fgo_goiymonan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpoonacularApiService {
    @GET("recipes/complexSearch")
    Call<RecipeResponse> searchRecipes(
            @Query("query") String query,
            @Query("number") int number,
            @Query("apiKey") String apiKey
    );
}
