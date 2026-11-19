package com.example.minipeta;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LostItemActivity extends AppCompatActivity {

    private EditText etProductName, etProductDesc;
    private Button btnPublish;
    private ImageView ivItemImage;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_item);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        NavigationHelper.setupNavigation(this);

        etProductName = findViewById(R.id.etProductName);
        etProductDesc = findViewById(R.id.etProductDesc);
        Button btnAddImage = findViewById(R.id.btnAddImage);
        ivItemImage = findViewById(R.id.ivItemImage);
        View imageDropContainer = findViewById(R.id.imageDropContainer);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if (imageUri != null && ivItemImage != null) {
                            ivItemImage.setImageURI(imageUri);
                        }
                    }
                }
        );

        View.OnClickListener pickImageListener = v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            imagePickerLauncher.launch(intent);
        };

        if (btnAddImage != null) btnAddImage.setOnClickListener(pickImageListener);
        if (ivItemImage != null) ivItemImage.setOnClickListener(pickImageListener);

        // Drag and Drop support for images (e.g. dragging straight from browser/Google)
        View.OnDragListener dragListener = (v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST) ||
                            event.getClipDescription().hasMimeType("image/*") ||
                            event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_LOCATION:
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
                case DragEvent.ACTION_DROP:
                    ClipData clipData = event.getClipData();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        ClipData.Item item = clipData.getItemAt(0);
                        Uri uri = item.getUri();
                        if (uri != null) {
                            imageUri = uri;
                            if (ivItemImage != null) {
                                ivItemImage.setImageURI(imageUri);
                            }
                            return true;
                        }
                        CharSequence text = item.getText();
                        if (text != null) {
                            try {
                                Uri parsedUri = Uri.parse(text.toString());
                                if (parsedUri != null) {
                                    imageUri = parsedUri;
                                    if (ivItemImage != null) {
                                        ivItemImage.setImageURI(imageUri);
                                    }
                                    return true;
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                    return false;
            }
            return false;
        };

        if (imageDropContainer != null) imageDropContainer.setOnDragListener(dragListener);
        if (ivItemImage != null) ivItemImage.setOnDragListener(dragListener);

        Button btnTheme = findViewById(R.id.btnTheme);
        if (btnTheme != null) {
            btnTheme.setOnClickListener(v -> NavigationHelper.showThemeDialog(this));
        }

        Button btnCancel = findViewById(R.id.btnCancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        btnPublish = findViewById(R.id.btnPublish);
        if (btnPublish != null) {
            btnPublish.setOnClickListener(v -> publishItem());
        }
    }

    private void publishItem() {
        if (etProductName == null || etProductDesc == null) return;

        String productName = etProductName.getText().toString().trim();
        String productDesc = etProductDesc.getText().toString().trim();

        if (productName.isEmpty()) {
            etProductName.setError("Enter product name");
            etProductName.requestFocus();
            return;
        }

        if (productDesc.isEmpty()) {
            etProductDesc.setError("Enter product description");
            etProductDesc.requestFocus();
            return;
        }

        if (btnPublish != null) {
            btnPublish.setEnabled(false);
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            auth.signInAnonymously()
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser newUser = authResult.getUser();
                        String userId = newUser != null ? newUser.getUid() : "anonymous";
                        executePublish(productName, productDesc, userId);
                    })
                    .addOnFailureListener(e -> {
                        if (btnPublish != null) btnPublish.setEnabled(true);
                        Toast.makeText(this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            executePublish(productName, productDesc, user.getUid());
        }
    }

    private void executePublish(String productName, String productDesc, String userId) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("lost_item_images/" + UUID.randomUUID().toString() + ".jpg");

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> saveItemToFirestore(productName, productDesc, userId, uri.toString()))
                            .addOnFailureListener(e -> {
                                if (btnPublish != null) btnPublish.setEnabled(true);
                                Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }))
                    .addOnFailureListener(e -> {
                        if (btnPublish != null) btnPublish.setEnabled(true);
                        Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            saveItemToFirestore(productName, productDesc, userId, "");
        }
    }

    private void saveItemToFirestore(String productName, String productDesc, String userId, String imageUrl) {
        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("productName", productName);
        itemMap.put("description", productDesc);
        itemMap.put("userId", userId);
        itemMap.put("imageUrl", imageUrl);
        itemMap.put("timestamp", FieldValue.serverTimestamp());

        db.collection("lost_items")
                .add(itemMap)
                .addOnSuccessListener(documentReference -> {
                    if (btnPublish != null) {
                        btnPublish.setEnabled(true);
                    }
                    Toast.makeText(this, "Lost item published successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    if (btnPublish != null) {
                        btnPublish.setEnabled(true);
                    }
                    Toast.makeText(this, "Failed to publish: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
