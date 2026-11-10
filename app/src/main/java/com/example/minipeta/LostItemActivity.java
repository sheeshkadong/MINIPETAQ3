package com.example.minipeta;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LostItemActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private EditText etProductName;
    private EditText etProductDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_item);

        databaseHelper = new DatabaseHelper(this);
        etProductName = findViewById(R.id.etProductName);
        etProductDesc = findViewById(R.id.etProductDesc);

        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        Button btnPublish = findViewById(R.id.btnPublish);
        if (btnPublish != null) {
            btnPublish.setOnClickListener(v -> saveLostItem());
        }
    }

    private void saveLostItem() {
        String productName = etProductName.getText().toString().trim();
        String description = etProductDesc.getText().toString().trim();

        if (productName.isEmpty()) {
            etProductName.setError("Please enter the item name");
            etProductName.requestFocus();
            return;
        }

        long result = databaseHelper.insertLostItem(productName, description);

        if (result != -1) {
            Toast.makeText(this, "Lost item report saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Unable to save report. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
