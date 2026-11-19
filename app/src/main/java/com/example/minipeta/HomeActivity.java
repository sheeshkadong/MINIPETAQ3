package com.example.minipeta;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationHelper.setupNavigation(this);

        Button btnTheme = findViewById(R.id.btnTheme);
        if (btnTheme != null) {
            btnTheme.setOnClickListener(v -> NavigationHelper.showThemeDialog(this));
        }

        TextView tvLostDescLink = findViewById(R.id.tvLostDescLink);
        if (tvLostDescLink != null) {
            tvLostDescLink.setOnClickListener(v -> startActivity(new Intent(this, LostItemActivity.class)));
        }

        Button btnReport = findViewById(R.id.btnReport);
        if (btnReport != null) {
            btnReport.setOnClickListener(v ->
                    startActivity(new Intent(this, LostItemActivity.class)));
        }

        Button btnDashboard = findViewById(R.id.btnDashboard);
        if (btnDashboard != null) {
            btnDashboard.setOnClickListener(v ->
                    startActivity(new Intent(this, DashboardActivity.class)));
        }

        // Category Cards
        setupCategoryCard(R.id.cardJackets, getString(R.string.cat_jackets));
        setupCategoryCard(R.id.cardWallets, getString(R.string.cat_wallets));
        setupCategoryCard(R.id.cardElectronics, getString(R.string.cat_electronics));
        setupCategoryCard(R.id.cardTowels, getString(R.string.cat_towels));
        setupCategoryCard(R.id.cardTumbler, getString(R.string.cat_tumbler));
        setupCategoryCard(R.id.cardMakeup, getString(R.string.cat_makeup));
        setupCategoryCard(R.id.cardUmbrella, getString(R.string.cat_umbrella));
        setupCategoryCard(R.id.cardAccessories, getString(R.string.cat_accessories));

        // Search Button & Bar
        EditText etSearch = findViewById(R.id.etSearch);
        Button btnSearch = findViewById(R.id.btnSearch);
        if (btnSearch != null && etSearch != null) {
            btnSearch.setOnClickListener(v -> {
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(this, CategoryItemsActivity.class);
                    intent.putExtra("category_name", query);
                    startActivity(intent);
                }
            });
        }
    }

    private void setupCategoryCard(int cardId, String categoryName) {
        View card = findViewById(cardId);
        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, CategoryItemsActivity.class);
                intent.putExtra("category_name", categoryName);
                startActivity(intent);
            });
        }
    }
}
