package com.example.minipeta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Report lost item button
        Button btnReport = findViewById(R.id.btnReport);
        if (btnReport != null) {
            btnReport.setOnClickListener(v ->
                    startActivity(new Intent(this, LostItemActivity.class)));
        }

        // View dashboard button
        Button btnDashboard = findViewById(R.id.btnDashboard);
        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(v ->
                    startActivity(new Intent(this, DashboardActivity.class)));
        }

        // Search button
        Button btnSearch = findViewById(R.id.btnSearch);
        EditText etSearch = findViewById(R.id.etSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> {
                String query = etSearch != null ? etSearch.getText().toString().trim() : "";
                if (!query.isEmpty()) {
                    Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Profile link in top bar
        TextView tvProfile = findViewById(R.id.tvProfile);
        if (tvProfile != null) {
            tvProfile.setOnClickListener(v ->
                    Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show());
        }

        // Category Cards click listeners
        setupCategoryCards();
    }

    private void setupCategoryCards() {
        bindCategoryCard(R.id.cardJackets, "Jackets");
        bindCategoryCard(R.id.cardWallets, "Wallets");
        bindCategoryCard(R.id.cardElectronics, "Electronics");
        bindCategoryCard(R.id.cardTowels, "Hand towels");
        bindCategoryCard(R.id.cardTumbler, "Tumbler");
        bindCategoryCard(R.id.cardMakeup, "Makeup");
        bindCategoryCard(R.id.cardUmbrella, "Umbrella");
        bindCategoryCard(R.id.cardAccessories, "Accessories");
    }

    private void bindCategoryCard(int cardId, String categoryName) {
        View card = findViewById(cardId);
        if (card != null) {
            card.setOnClickListener(v ->
                    Toast.makeText(this, categoryName + " category selected", Toast.LENGTH_SHORT).show());
        }
    }
}