package com.example.fgo_goiymonan;

import java.util.List;

public class RecipeDetail {
    private List<ExtendedIngredient> extendedIngredients;

    public List<ExtendedIngredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public static class ExtendedIngredient {
        private String original;

        public String getOriginal() {
            return original;
        }
    }
}
