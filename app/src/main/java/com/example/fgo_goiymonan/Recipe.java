package com.example.fgo_goiymonan;

import java.io.Serializable;
import java.util.Objects;

public class Recipe implements Serializable{
    private int id;
    private String title;
    private String image;
    private String ingredients; // Thêm thuộc tính để lưu nguyên liệu

    private int readyInMinutes;
    private String instructions;
    public Recipe(int id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.ingredients = " ";
        this.readyInMinutes = 0;
        this.instructions = "";
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(int readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recipe recipe = (Recipe) obj;
        return id == recipe.id;  // dựa vào id duy nhất
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
