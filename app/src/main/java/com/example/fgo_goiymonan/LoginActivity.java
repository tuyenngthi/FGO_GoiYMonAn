package com.example.fgo_goiymonan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private TextView tvEmailError, tvPasswordError;
    private Button btnLogin;
    private TextView tvSignUp, tvForgotPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        //Lấy combonent từ id
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp2);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setText(Html.fromHtml("<u>Forgot Password?</u>", Html.FROM_HTML_MODE_LEGACY));
        tvSignUp.setText(Html.fromHtml("<u>Sign up here</u>", Html.FROM_HTML_MODE_LEGACY));

        //Khởi động FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                //Email đã xác minh, cho phép đăng nhập
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            else {
                //Email chưa xác minh, yêu cầu xác minh và đăng xuất
                Toast.makeText(this, "Please verify email: " + currentUser.getEmail(), Toast.LENGTH_LONG).show();
                currentUser.sendEmailVerification();
                mAuth.signOut();
            }
        }
        else {
            mAuth.signOut();
        }

        //Lấy combonent từ id
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvEmailError = findViewById(R.id.tvEmailError);
        tvPasswordError = findViewById(R.id.tvPasswordError);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp2);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        //Ẩn hiện mật khẩu
        setupPasswordToggle(etPassword);

        //Bắt sự kiện cho nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            //Ẩn tất cả thông báo lỗi trước khi kiểm tra
            tvEmailError.setVisibility(View.GONE);
            tvPasswordError.setVisibility(View.GONE);

            //Kiểm tra đầu vào
            if (validateInput(email, password)) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            //nếu user đã xác minh thì cho phép đăng nhập
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                //nếu email chưa xác minh thì thông báo và gửi lại mail xác minh
                                Toast.makeText(LoginActivity.this, "Please verify email! Verification email has been resent.", Toast.LENGTH_LONG).show();
                                user.sendEmailVerification().addOnCompleteListener(task_ -> {
                                    if (task_.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Verification email has been resent. Please check your inbox!", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this, "Unable to send verification email: " + task_.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                mAuth.signOut(); //Đăng xuất để người dùng kh thể truy cập
                            }
                        }
                    }
                    else {
                        tvPasswordError.setText("Incorrect email or password!");
                        tvPasswordError.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //Thêm sự kiện nhấn cho TextView Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {


            // Chuyển sang ForgotPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });


        //Thêm sự kiện nhấn cho TextView Đăng ký
        tvSignUp.setOnClickListener(v -> {
            //Chuyển sang SignUpActivity
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Chuyển đổi ẩn hiện mật khẩu
    private void setupPasswordToggle(EditText editText) {
        //Lưu trạng thái ẩn/hiện mật khẩu trong Tag của EditText - mặc đinh là ẩn
        editText.setTag(false);

        //bắt sự kiện
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                double touchAreaStart = editText.getRight() - editText.getCompoundDrawables()[2].getBounds().width() - editText.getPaddingRight();
                if (event.getRawX() >= touchAreaStart) {
                    boolean isVisible = (boolean) editText.getTag();
                    isVisible = !isVisible;

                    if (isVisible) {
                        editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
                    }
                    else {
                        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                    }

                    //cập nhật trạng thái
                    editText.setTag(isVisible);
                    //di chuyển con trỏ tới cuối văn bản
                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        //Kiểm tra email
        if (email.isEmpty()) {
            tvEmailError.setText("The email cannot be empty.");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmailError.setText("Invalid email.");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        //Kiểm tra mật khẩu
        if (password.isEmpty()) {
            tvPasswordError.setText("The password cannot be empty.");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else if (password.length() < 6 || password.length() >= 4096) {
            tvPasswordError.setText("The password must be at least 6 characters long.");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else {
            //Kiểm tra kí tự thường, kí tự hoa, số, kí tự đặc biệt
            boolean hasLowerCase = password.matches(".*[a-z].*");
            boolean hasUpperCase = password.matches(".*[A-Z].*");
            boolean hasDigit = password.matches(".*[0-9].*");
            boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
            if (!hasSpecialChar || !hasDigit || !hasLowerCase || !hasUpperCase) {
                tvPasswordError.setText("The password must contain uppercase letters, lowercase letters, numbers, and special characters.");
                tvPasswordError.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }
        return isValid;
    }


}