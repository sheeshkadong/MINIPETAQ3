package com.example.minipeta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getWindow() != null) {
            WindowCompat.getInsetsController(
                    getWindow(),
                    getWindow().getDecorView()
            ).setAppearanceLightStatusBars(false);
        }

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Connect existing XML components
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        TextView tvRegister = findViewById(R.id.tvRegister);

        // Login button
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> loginUser());
        }

        // Forgot password
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> resetPassword());
        }

        // Registration screen
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class))
            );
        }
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.login_enter_email));
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.login_enter_password));
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText(R.string.login_logging_in);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    btnLogin.setEnabled(true);
                    btnLogin.setText(R.string.login_button);

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                R.string.login_successful,
                                Toast.LENGTH_SHORT
                        ).show();

                        // Continue to the existing home screen.
                        startActivity(
                                new Intent(this, HomeActivity.class)
                        );

                        finish();

                    } else {

                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : getString(R.string.login_failed);

                        Toast.makeText(
                                this,
                                error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void resetPassword() {

        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.login_enter_email_first));
            etEmail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                this,
                                R.string.login_reset_email_sent,
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                R.string.login_reset_email_unable,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}