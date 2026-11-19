package com.example.minipeta;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NavigationHelper {

    public static void setupNavigation(AppCompatActivity activity) {
        ImageView ivTopLogo = activity.findViewById(R.id.ivTopLogo);
        TextView tvTopTitle = activity.findViewById(R.id.tvTopTitle);
        TextView tvProfile = activity.findViewById(R.id.tvProfile);

        View.OnClickListener goHome = v -> {
            if (!(activity instanceof HomeActivity)) {
                Intent intent = new Intent(activity, HomeActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        };

        if (ivTopLogo != null) ivTopLogo.setOnClickListener(goHome);
        if (tvTopTitle != null) tvTopTitle.setOnClickListener(goHome);

        if (tvProfile != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getEmail() != null) {
                tvProfile.setText(currentUser.getEmail());
            }
            tvProfile.setOnClickListener(v -> showProfileMenu(activity));
        }

        View btnNavHome = activity.findViewById(R.id.btnNavHome);
        View btnNavReport = activity.findViewById(R.id.btnNavReport);
        View btnNavDashboard = activity.findViewById(R.id.btnNavDashboard);

        TextView tvNavHome = activity.findViewById(R.id.tvNavHome);
        TextView tvNavReport = activity.findViewById(R.id.tvNavReport);
        TextView tvNavDashboard = activity.findViewById(R.id.tvNavDashboard);

        if (btnNavHome != null) btnNavHome.setOnClickListener(goHome);

        if (btnNavReport != null) {
            btnNavReport.setOnClickListener(v -> {
                if (!(activity instanceof LostItemActivity)) {
                    Intent intent = new Intent(activity, LostItemActivity.class);
                    activity.startActivity(intent);
                }
            });
        }

        if (btnNavDashboard != null) {
            btnNavDashboard.setOnClickListener(v -> {
                if (!(activity instanceof DashboardActivity)) {
                    Intent intent = new Intent(activity, DashboardActivity.class);
                    activity.startActivity(intent);
                }
            });
        }

        int activeColor = ContextCompat.getColor(activity, R.color.purple);
        if (activity instanceof HomeActivity && tvNavHome != null) {
            tvNavHome.setTextColor(activeColor);
        } else if (activity instanceof LostItemActivity && tvNavReport != null) {
            tvNavReport.setTextColor(activeColor);
        } else if (activity instanceof DashboardActivity && tvNavDashboard != null) {
            tvNavDashboard.setTextColor(activeColor);
        }
    }

    private static void showProfileMenu(AppCompatActivity activity) {
        String[] options = {
                "Home Screen",
                "Admin Dashboard",
                "Report Lost Item",
                "Change Appearance",
                "Log Out"
        };

        new AlertDialog.Builder(activity)
                .setTitle("Navigation & Settings")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if (!(activity instanceof HomeActivity)) {
                                activity.startActivity(new Intent(activity, HomeActivity.class));
                                activity.finish();
                            }
                            break;
                        case 1:
                            if (!(activity instanceof DashboardActivity)) {
                                activity.startActivity(new Intent(activity, DashboardActivity.class));
                            }
                            break;
                        case 2:
                            if (!(activity instanceof LostItemActivity)) {
                                activity.startActivity(new Intent(activity, LostItemActivity.class));
                            }
                            break;
                        case 3:
                            showThemeDialog(activity);
                            break;
                        case 4:
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                            break;
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public static void showThemeDialog(AppCompatActivity activity) {
        String[] themes = {"Light Mode", "Dark Mode", "System Default"};
        String current = ThemeManager.getTheme(activity);
        int selected = ThemeManager.LIGHT.equals(current) ? 0 : (ThemeManager.DARK.equals(current) ? 1 : 2);

        new AlertDialog.Builder(activity)
                .setTitle("Choose Appearance")
                .setSingleChoiceItems(themes, selected, (dialog, which) -> {
                    String theme = (which == 0) ? ThemeManager.LIGHT : ((which == 1) ? ThemeManager.DARK : ThemeManager.SYSTEM);
                    ThemeManager.setTheme(activity, theme);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}