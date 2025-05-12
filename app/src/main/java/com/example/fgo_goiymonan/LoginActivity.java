package com.example.fgo_goiymonan;

import android.content.Intent;
import android.os.Bundle;
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

        //Khởi động FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                //Email đã xác minh, cho phép đăng nhập
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            else {
                //Email chưa xác minh, yêu cầu xác minh và đăng xuất
                Toast.makeText(this, "Vui lòng xác minh email: " + currentUser.getEmail(), Toast.LENGTH_LONG).show();
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
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                //nếu email chưa xác minh thì thông báo và gửi lại mail xác minh
                                Toast.makeText(LoginActivity.this, "Vui lòng xác minh email! Email xác minh đã được gửi lại.", Toast.LENGTH_LONG).show();
                                user.sendEmailVerification().addOnCompleteListener(task_ -> {
                                    if (task_.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Email xác minh đã được gửi lại. Vui lòng kiểm tra hộp thư!", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this, "Không thể gửi email xác minh: " + task_.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                mAuth.signOut(); //Đăng xuất để người dùng kh thể truy cập
                            }
                        }
                    }
                    else {
                        tvPasswordError.setText("Email hoặc mật khẩu sai!");
                        tvPasswordError.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //Thêm sự kiện nhấn cho TextView Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            //Chuyển sang giao diện quên mật khẩu
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
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
            tvEmailError.setText("Email không được để trống.");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tvEmailError.setText("Email không hợp lệ.");
            tvEmailError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        //Kiểm tra mật khẩu
        if (password.isEmpty()) {
            tvPasswordError.setText("Mật khẩu không được để trống.");
            tvPasswordError.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else if (password.length() < 6 || password.length() >= 4096) {
            tvPasswordError.setText("Mật khẩu phải có ít nhất 6 ký tự.");
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
                tvPasswordError.setText("Mật khẩu phải chứa kí tự in hoa, kí tự thường, số và kí tự đặc biệt.");
                tvPasswordError.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }
        return isValid;
    }
}