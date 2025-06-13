package com.example.fgo_goiymonan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {
    private TextView tvSearch, tvFavorite, tvViewed;
    private Button btnLogout, btnChangePassword;
    private TextView tvEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        //Lấy combonent từ id
        tvSearch = findViewById(R.id.tvSearch);
        tvFavorite = findViewById(R.id.tvFavorite);
        tvViewed = findViewById(R.id.tvViewed);
        btnLogout = findViewById(R.id.btnLogout);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        tvEmail = findViewById(R.id.tvEmail);

        //Bắt sự kiện chuyển gd
        tvSearch.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        tvFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        tvViewed.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ViewedActivity.class);
            startActivity(intent);
        });

        //Khởi tạo mAuth
        mAuth = FirebaseAuth.getInstance();

        //Kiểm tra trạng thái đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || !currentUser.isEmailVerified()) {
            mAuth.signOut();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //hiển thị email
        tvEmail.setText(currentUser.getEmail());

        //Bắt sự kiện cho nút đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        //Bắt sự kiện cho nút đăng xuất
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}