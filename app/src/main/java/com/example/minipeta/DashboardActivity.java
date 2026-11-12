package com.example.minipeta;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeManager.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button btnTheme = findViewById(R.id.btnTheme);

        if (btnTheme != null) {
            btnTheme.setOnClickListener(v -> showThemeOptions());
        }
    }

    private void showThemeOptions() {

        String[] themes = {
                getString(R.string.dash_light_mode),
                getString(R.string.dash_dark_mode),
                getString(R.string.dash_system_default)
        };

        String currentTheme = ThemeManager.getTheme(this);

        int selected;

        if (Objects.equals(currentTheme, ThemeManager.LIGHT)) {
            selected = 0;
        } else if (Objects.equals(currentTheme, ThemeManager.DARK)) {
            selected = 1;
        } else {
            selected = 2;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.dash_choose_appearance)
                .setSingleChoiceItems(themes, selected, (dialog, which) -> {

                    String theme;

                    if (which == 0) {
                        theme = ThemeManager.LIGHT;
                    } else if (which == 1) {
                        theme = ThemeManager.DARK;
                    } else {
                        theme = ThemeManager.SYSTEM;
                    }

                    ThemeManager.setTheme(this, theme);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}