package com.example.fgo_goiymonan;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView ivRecipeImage;
    private TextView tvRecipeName, tvIngredients, tvTime, tvProcedure;

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail); // Thay bằng tên đúng XML của bạn

        ivRecipeImage = findViewById(R.id.ivRecipeImage);
        tvRecipeName = findViewById(R.id.tvRecipeName);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvTime = findViewById(R.id.tvTime);
        tvProcedure = findViewById(R.id.tvProcedure);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            // Kết thúc activity hiện tại, quay về màn hình trước
            finish();
        });

        // Nhận Recipe từ Intent
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        if (recipe != null) {
            bindData(recipe);
            ViewedStorage.addViewedRecipe(this, recipe); // lưu vào lịch sử
        }
    }

    private void bindData(Recipe recipe) {
        // Load ảnh
        Picasso.get().load(recipe.getImage()).into(ivRecipeImage);

        // Tên món
        tvRecipeName.setText(recipe.getTitle());

        // Nguyên liệu
        tvIngredients.setText(recipe.getIngredients());

        // Thời gian
        tvTime.setText(recipe.getReadyInMinutes() + " minutes");

        // Procedure - xử lý HTML
        String instructions = recipe.getInstructions() != null ? recipe.getInstructions() : "No instructions available.";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvProcedure.setText(Html.fromHtml(instructions, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvProcedure.setText(Html.fromHtml(instructions));
        }
    }
}