package com.example.fgo_goiymonan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private TextView emailErrorTextView, passwordErrorTextView;
    private Button logInButton;
    private TextView signUpTextView, forgotPasswordTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Lấy combonent từ id
        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        emailErrorTextView = findViewById(R.id.tvEmailError);
        passwordErrorTextView = findViewById(R.id.tvPasswordError);
        logInButton = findViewById(R.id.btnLogin);
        signUpTextView = findViewById(R.id.tvSignUp2);
        forgotPasswordTextView = findViewById(R.id.tvForgotPassword);

        //Khởi động FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                //Email đã xác minh, cho phép đăng nhập
                Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
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
        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        emailErrorTextView = findViewById(R.id.tvEmailError);
        passwordErrorTextView = findViewById(R.id.tvPasswordError);
        logInButton = findViewById(R.id.btnLogin);
        signUpTextView = findViewById(R.id.tvSignUp2);
        forgotPasswordTextView = findViewById(R.id.tvForgotPassword);

        //Ẩn hiện mật khẩu
        setupPasswordToggle(passwordEditText);

        //Bắt sự kiện cho nút Đăng nhập
        logInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            //Ẩn tất cả thông báo lỗi trước khi kiểm tra
            emailErrorTextView.setVisibility(View.GONE);
            passwordErrorTextView.setVisibility(View.GONE);

            //Kiểm tra đầu vào
            if (validateInput(email, password)) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            //nếu user đã xác minh thì cho phép đăng nhập
                            if (user.isEmailVerified()) {
                                Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                //nếu email chưa xác minh thì thông báo và gửi lại mail xác minh
                                Toast.makeText(MainActivity.this, "Vui lòng xác minh email! Email xác minh đã được gửi lại.", Toast.LENGTH_LONG).show();
                                user.sendEmailVerification().addOnCompleteListener(task_ -> {
                                    if (task_.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Email xác minh đã được gửi lại. Vui lòng kiểm tra hộp thư!", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Không thể gửi email xác minh: " + task_.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                mAuth.signOut(); //Đăng xuất để người dùng kh thể truy cập
                            }
                        }
                    }
                    else {
                        passwordErrorTextView.setText("Email hoặc mật khẩu sai!");
                        passwordErrorTextView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //Thêm sự kiện nhấn cho TextView Quên mật khẩu
        forgotPasswordTextView.setOnClickListener(v -> {
            //Chuyển sang giao diện quên mật khẩu
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        //Thêm sự kiện nhấn cho TextView Đăng ký
        signUpTextView.setOnClickListener(v -> {
            //Chuyển sang SignUpActivity
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
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
            emailErrorTextView.setText("Email không được để trống.");
            emailErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErrorTextView.setText("Email không hợp lệ.");
            emailErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        }

        //Kiểm tra mật khẩu
        if (password.isEmpty()) {
            passwordErrorTextView.setText("Mật khẩu không được để trống.");
            passwordErrorTextView.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else if (password.length() < 6 || password.length() >= 4096) {
            passwordErrorTextView.setText("Mật khẩu phải có ít nhất 6 ký tự.");
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
                passwordErrorTextView.setText("Mật khẩu phải chứa kí tự in hoa, kí tự thường, số và kí tự đặc biệt.");
                passwordErrorTextView.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }
        return isValid;
    }


}

