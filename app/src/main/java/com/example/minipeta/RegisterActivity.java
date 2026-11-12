package com.example.minipeta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> registerUser());
        }

        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> finish());
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.reg_enter_name));
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.reg_enter_email));
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.reg_enter_password));
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError(getString(R.string.reg_password_min));
            etPassword.requestFocus();
            return;
        }

        if (!Objects.equals(password, confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.reg_password_mismatch));
            etConfirmPassword.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText(R.string.reg_creating_account);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {
                        restoreRegisterButton();

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : getString(R.string.reg_failed);

                        Toast.makeText(
                                this,
                                error,
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    if (auth.getCurrentUser() == null) {
                        restoreRegisterButton();
                        Toast.makeText(
                                this,
                                R.string.reg_user_retrieval_failed,
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    String uid = auth.getCurrentUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("role", "user");
                    user.put("createdAt", FieldValue.serverTimestamp());

                    db.collection("users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(
                                        this,
                                        R.string.reg_success,
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        RegisterActivity.this,
                                        HomeActivity.class
                                );

                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );

                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                restoreRegisterButton();

                                Toast.makeText(
                                        this,
                                        R.string.reg_profile_failed,
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                });
    }

    private void restoreRegisterButton() {
        btnRegister.setEnabled(true);
        btnRegister.setText(R.string.reg_btn_create);
    }
}