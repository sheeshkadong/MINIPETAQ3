package com.example.minipeta;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        verifyAdminAccess();
    }

    private void verifyAdminAccess() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            redirectToLogin();
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        redirectToHome();
                        return;
                    }

                    String role = document.getString("role");

                    if ("admin".equals(role)) {
                        setContentView(R.layout.activity_dashboard);

                        NavigationHelper.setupNavigation(this);

                        Button btnTheme = findViewById(R.id.btnTheme);
                        if (btnTheme != null) {
                            btnTheme.setOnClickListener(v -> NavigationHelper.showThemeDialog(this));
                        }
                    } else {
                        redirectToHome();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Unable to verify administrator access.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void redirectToHome() {
        Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
