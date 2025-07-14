package com.example.prm391_project.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.prm391_project.databinding.ActivityLoginBinding;
import com.example.prm391_project.helper.ManagementUser;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
    }

    private void setVariable() {
        ManagementUser mgmtUser = new ManagementUser(this);
        binding.loginBtn.setOnClickListener(view -> {
            String email = binding.userEdt.getText().toString().trim();
            String password = binding.passEdt.getText().toString().trim();

            if (validateInput(email, password)) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        mgmtUser.saveUser(email, password);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish(); // Finish the activity to prevent going back to it
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, kiểm tra lại email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.textView8.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        // Setup the fingerprint button click listener
        if (mgmtUser.userSaved()) {
            binding.fingerprintBtn.setOnClickListener(view -> authenticateUser());
        } else {
            binding.fingerprintBtn.setOnClickListener(view -> {
                Toast.makeText(LoginActivity.this, "Vui lòng đăng nhập trước khi xác thực vân tay!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void authenticateUser() {
        ManagementUser mgmtUser = new ManagementUser(this);

        // Initialize BiometricPrompt
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Successful authentication
                mAuth.signInWithEmailAndPassword(mgmtUser.getEmail(), mgmtUser.getPassword()).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish(); // Finish the activity to prevent going back to it
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, kiểm tra lại email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LoginActivity.this, "Lỗi xác thực: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // Create the BiometricPrompt.PromptInfo
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập sinh trắc học")
                .setSubtitle("Đăng nhập bằng thông tin sinh trắc học của bạn")
                .setDeviceCredentialAllowed(true)
                .build();

        // Show the biometric prompt
        biometricPrompt.authenticate(promptInfo);
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Mật khẩu không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
