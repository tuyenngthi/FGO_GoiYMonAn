package com.example.fgo_goiymonan;

public class Recipe {
    private int id;
    private String title;
    private String image;
    private String ingredients; // Thêm thuộc tính để lưu nguyên liệu

    public Recipe(int id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.ingredients = " ";
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
}
