package com.example.minipeta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        TextView tvRegister = findViewById(R.id.tvRegister);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> loginUser());
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> resetPassword());
        }

        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        }
    }

    private void loginUser() {
        if (etEmail == null || etPassword == null) return;

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Enter your email.");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter your password.");
            etPassword.requestFocus();
            return;
        }

        if (btnLogin != null) btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    if (auth.getCurrentUser() == null) {
                        if (btnLogin != null) btnLogin.setEnabled(true);
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();

                    db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(document -> {
                                if (btnLogin != null) btnLogin.setEnabled(true);

                                if (!document.exists()) {
                                    auth.signOut();
                                    Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String role = document.getString("role");

                                if ("admin".equals(role)) {
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    finish();
                                } else if ("user".equals(role)) {
                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    auth.signOut();
                                    Toast.makeText(this, "Invalid account role.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (btnLogin != null) btnLogin.setEnabled(true);
                                Toast.makeText(this, "Unable to verify account role.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    if (btnLogin != null) btnLogin.setEnabled(true);
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void resetPassword() {
        if (etEmail == null) return;

        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Enter your email first.");
            etEmail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Unable to send reset email: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}