package com.example.fgo_goiymonan;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();

            // Kiểm tra rỗng
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem email có tồn tại trên Firebase không
            auth.fetchSignInMethodsForEmail(email)
                    .addOnSuccessListener(result -> {
                        if (result.getSignInMethods() == null || result.getSignInMethods().isEmpty()) {
                            Toast.makeText(this, "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                        } else {
                            // Gửi email reset password
                            auth.sendPasswordResetEmail(email)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Email đổi mật khẩu đã được gửi", Toast.LENGTH_LONG).show();
                                        finish(); // quay lại màn hình login
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Lỗi gửi email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi kiểm tra email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
