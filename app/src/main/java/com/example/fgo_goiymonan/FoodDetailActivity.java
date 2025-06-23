package com.example.fgo_goiymonan;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView ivRecipeImage;
    private TextView tvRecipeName, tvIngredients, tvTime, tvProcedure;
    private ImageButton btnBack, btnFavorite;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private int id;
    private String title, image, ingredients, instructions;
    private int readyInMinutes;

    private boolean isFavorite = false;  // kiểm tra trạng thái yêu thích

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Ánh xạ view
        ivRecipeImage = findViewById(R.id.ivRecipeImage);
        tvRecipeName = findViewById(R.id.tvRecipeName);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvTime = findViewById(R.id.tvTime);
        tvProcedure = findViewById(R.id.tvProcedure);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu từ Intent
        id = getIntent().getIntExtra("id", 0);
        title = getIntent().getStringExtra("title");
        image = getIntent().getStringExtra("image");
        ingredients = getIntent().getStringExtra("ingredients");
        readyInMinutes = getIntent().getIntExtra("readyInMinutes", 0);
        instructions = getIntent().getStringExtra("instructions");

        // Hiển thị dữ liệu
        Picasso.get().load(image).into(ivRecipeImage);
        tvRecipeName.setText(title);
        tvIngredients.setText(ingredients);
        tvTime.setText(readyInMinutes + " minutes");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvProcedure.setText(Html.fromHtml(instructions, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvProcedure.setText(Html.fromHtml(instructions));
        }

        // Gọi lưu vào Firestore Viewed
        saveToViewed();

        // Kiểm tra xem đã nằm trong Favorites chưa
        checkFavoriteStatus();

        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void checkFavoriteStatus() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(uid)
                .collection("favorites").document(String.valueOf(id));

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                isFavorite = true;
                btnFavorite.setImageResource(R.drawable.like);
            } else {
                isFavorite = false;
                btnFavorite.setImageResource(R.drawable.unlike);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error checking favorite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleFavorite() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(uid)
                .collection("favorites").document(String.valueOf(id));

        if (isFavorite) {
            docRef.delete().addOnSuccessListener(unused -> {
                isFavorite = false;
                btnFavorite.setImageResource(R.drawable.unlike);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to remove: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Map<String, Object> favorite = new HashMap<>();
            favorite.put("id", id);
            favorite.put("title", title);
            favorite.put("image", image);
            favorite.put("ingredients", ingredients);
            favorite.put("readyInMinutes", readyInMinutes);
            favorite.put("instructions", instructions);

            docRef.set(favorite).addOnSuccessListener(unused -> {
                isFavorite = true;
                btnFavorite.setImageResource(R.drawable.like);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to add: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveToViewed() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference docRef = db.collection("users").document(uid)
                .collection("viewed").document(String.valueOf(id));

        Map<String, Object> viewed = new HashMap<>();
        viewed.put("id", id);
        viewed.put("title", title);
        viewed.put("image", image);
        viewed.put("ingredients", ingredients);
        viewed.put("readyInMinutes", readyInMinutes);
        viewed.put("instructions", instructions);

        docRef.set(viewed).addOnSuccessListener(aVoid -> {
            Log.d("Firestore", "Viewed saved");
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Failed to save viewed", e);
        });
    }
}
