package com.example.fgo_goiymonan;

import static android.text.InputType.TYPE_CLASS_TEXT;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private EditText passwordEditText, confirmPasswordEditText, emailEditText;
    private TextView emailErrorTextView, passwordErrorTextView, confirmPasswordErrorTextView, tvLogin2;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Lấy các combonent từ id
        passwordEditText = findViewById(R.id.etPassword);
        confirmPasswordEditText = findViewById(R.id.etConfirmPassword);
        emailEditText = findViewById(R.id.etEmail);
        signUpButton = findViewById(R.id.btnSignUp);
        emailErrorTextView = findViewById(R.id.tvEmailError);
        passwordErrorTextView = findViewById(R.id.tvPasswordError);
        confirmPasswordErrorTextView = findViewById(R.id.tvConfirmPasswordError);
        tvLogin2 = findViewById(R.id.tvLogin2);
        tvLogin2.setText(Html.fromHtml("<u>Log in now</u>", Html.FROM_HTML_MODE_LEGACY));

        //Chuyển đổi ẩn hiện mật khẩu bằng cách nhấn icon_eye
        setupPasswordToggle(passwordEditText);
        setupPasswordToggle(confirmPasswordEditText);

        //Triển khai chức năng đăng ký
        mAuth = FirebaseAuth.getInstance();
        signUpButton.setOnClickListener(v -> { //Sự kiện ấn nút đăng kí
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            //Ẩn tất cả thông báo lỗi trước khi kiểm tra
            emailErrorTextView.setVisibility(View.GONE);
            passwordErrorTextView.setVisibility(View.GONE);
            confirmPasswordErrorTextView.setVisibility(View.GONE);

            //Kiểm tra dữ liệu nhập vào
            if (validateInput(email, password, confirmPassword)) {
                //
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                //gửi email xác thực
                                user.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                                    if (verifyTask.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Registration successful. Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(SignUpActivity.this, "Unable to send verification email: " + verifyTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            //Chuyển sang MainActivity
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //Chuyển sang form Đăng nhập bằng việc nhấn vào textview Đăng nhập
        tvLogin2.setOnClickListener(v -> { //Bắt sự kiện cho TextView
            // Chuyển sang LogInActivity
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
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

    //Hàm xử lý đăng ký
    private boolean validateInput(String email, String      password, String confirmPassword) {
        boolean isValid = true;

        //Kiểm tra email
        if (email.isEmpty()) {
            emailErrorTextView.setText("The email cannot be empty..");
            emailErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErrorTextView.setText("Invalid email.");
            emailErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        }

        //Kiểm tra mật khẩu
        if (password.isEmpty()) {
            passwordErrorTextView.setText("The password cannot be empty.");
            passwordErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else if (password.length() < 6 || password.length() >= 4096) {
            passwordErrorTextView.setText("The password must be at least 6 characters long.");
            passwordErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else {
            //Kiểm tra kí tự thường, kí tự hoa, số, kí tự đặc biệt
            boolean hasLowerCase = password.matches(".*[a-z].*");
            boolean hasUpperCase = password.matches(".*[A-Z].*");
            boolean hasDigit = password.matches(".*[0-9].*");
            boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
            if (!hasSpecialChar || !hasDigit || !hasLowerCase || !hasUpperCase) {
                passwordErrorTextView.setText("The password must contain uppercase letters, lowercase letters, numbers, and special characters.");
                passwordErrorTextView.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        //Kiểm tra xác nhận mật khẩu
        if (confirmPassword.isEmpty()) {
            confirmPasswordErrorTextView.setText("Please comfirm the password.");
            confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordErrorTextView.setText("The confirm password does not match.");
            confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }
}