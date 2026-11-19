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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etName, etEmail, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> registerUser());
        }

        if (tvLogin != null) {
            tvLogin.setOnClickListener(v -> finish());
        }
    }

    private void registerUser() {
        String name = etName != null ? etName.getText().toString().trim() : "";
        String email = etEmail != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword != null ? etPassword.getText().toString().trim() : "";

        if (name.isEmpty()) {
            if (etName != null) {
                etName.setError("Enter your name");
                etName.requestFocus();
            }
            return;
        }

        if (email.isEmpty()) {
            if (etEmail != null) {
                etEmail.setError("Enter your email");
                etEmail.requestFocus();
            }
            return;
        }

        if (password.length() < 6) {
            if (etPassword != null) {
                etPassword.setError("Password must be at least 6 characters");
                etPassword.requestFocus();
            }
            return;
        }

        if (btnRegister != null) btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    if (auth.getCurrentUser() != null) {
                        String uid = auth.getCurrentUser().getUid();
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("email", email);
                        userMap.put("role", "user");

                        db.collection("users").document(uid).set(userMap)
                                .addOnSuccessListener(unused -> {
                                    if (btnRegister != null) btnRegister.setEnabled(true);
                                    startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                    finishAffinity();
                                })
                                .addOnFailureListener(e -> {
                                    if (btnRegister != null) btnRegister.setEnabled(true);
                                    Toast.makeText(this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    if (btnRegister != null) btnRegister.setEnabled(true);
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}