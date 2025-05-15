// ChangePasswordActivity.java
package com.example.fgo_goiymonan;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private TextView tvError;
    private ImageView ivToggleNew, ivToggleConfirm;
    private boolean isNewVisible = false, isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvError = findViewById(R.id.tvError);
        ivToggleNew = findViewById(R.id.ivToggleNewPassword);
        ivToggleConfirm = findViewById(R.id.ivToggleConfirmPassword);;
        Button btnChangePassword = findViewById(R.id.btnChangePassword);

        ivToggleNew.setOnClickListener(v -> togglePasswordVisibility(etNewPassword, ivToggleNew, isNewVisible = !isNewVisible));
        ivToggleConfirm.setOnClickListener(v -> togglePasswordVisibility(etConfirmPassword, ivToggleConfirm, isConfirmVisible = !isConfirmVisible));

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            showError("Bạn chưa đăng nhập.");
            return;
        }

        String oldPass = etOldPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showError("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showError("Mật khẩu mới không khớp.");
            return;
        }

        if (newPass.length() < 6) {
            showError("Mật khẩu phải từ 6 ký tự trở lên.");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(this, "Đổi mật khẩu thành công.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        showError("Lỗi khi đổi mật khẩu: " + updateTask.getException().getMessage());
                    }
                });
            } else {
                showError("Mật khẩu cũ không đúng.");
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView icon, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            icon.setImageResource(R.drawable.ic_eye_off);
        }
        editText.setSelection(editText.getText().length());
    }

    private void showError(String message) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }
}
