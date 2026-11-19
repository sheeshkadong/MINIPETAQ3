package com.example.minipeta;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CategoryItemsActivity extends AppCompatActivity {

    private LinearLayout containerItems;
    private FirebaseFirestore db;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_items);

        NavigationHelper.setupNavigation(this);

        categoryName = getIntent().getStringExtra("category_name");
        if (categoryName == null) categoryName = "Items";

        TextView tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        if (tvCategoryTitle != null) {
            tvCategoryTitle.setText(getString(R.string.category_title_format, categoryName));
        }

        containerItems = findViewById(R.id.containerItems);

        db = FirebaseFirestore.getInstance();
        loadCategoryItems();

        Button btnTheme = findViewById(R.id.btnTheme);
        if (btnTheme != null) {
            btnTheme.setOnClickListener(v -> NavigationHelper.showThemeDialog(this));
        }
    }

    private void loadCategoryItems() {
        db.collection("lost_items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (containerItems != null) {
                        containerItems.removeAllViews();
                    }

                    int matchCount = 0;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String productName = doc.getString("productName");
                        String description = doc.getString("description");

                        if (productName == null) productName = "";
                        if (description == null) description = "";

                        boolean matches = productName.toLowerCase().contains(categoryName.toLowerCase()) ||
                                description.toLowerCase().contains(categoryName.toLowerCase()) ||
                                categoryName.isEmpty();

                        if (matches) {
                            matchCount++;
                            addItemCard(productName, description);
                        }
                    }

                    if (matchCount == 0 && containerItems != null) {
                        TextView tvEmpty = new TextView(this);
                        tvEmpty.setText(getString(R.string.no_items_found, categoryName));
                        tvEmpty.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
                        tvEmpty.setPadding(16, 16, 16, 16);
                        containerItems.addView(tvEmpty);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load items: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addItemCard(String name, String desc) {
        if (containerItems == null) return;

        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(24, 24, 24, 24);

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextSize(16);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.purple_dark));
        card.addView(tvName);

        if (!desc.isEmpty()) {
            TextView tvDesc = new TextView(this);
            tvDesc.setText(desc);
            tvDesc.setTextSize(14);
            tvDesc.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
            tvDesc.setPadding(0, 8, 0, 0);
            card.addView(tvDesc);
        }

        containerItems.addView(card);
    }
}
