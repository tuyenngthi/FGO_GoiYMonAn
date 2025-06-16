package com.example.fgo_goiymonan;

import java.util.List;

public class RecipeDetail {
    private List<ExtendedIngredient> extendedIngredients;
    private int readyInMinutes;
    private String instructions;
    public List<ExtendedIngredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public String getInstructions() {
        return instructions;
    }
    public static class ExtendedIngredient {
        private String original;
        private String name;

        public String getOriginal() {
            return original;
        }

        public String getName() {
            return name;
        }
    }
}
