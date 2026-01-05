package com.moorixlabs.park;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.moorixlabs.park.models.User;
import com.moorixlabs.park.models.UserManager;
import com.moorixlabs.park.utils.LanguageHelper;
import com.moorixlabs.park.utils.UserPreferences;

public class ProfileActivity extends AppCompatActivity {

    private UserManager userManager;
    private TextView tvInitials, tvUserName, tvUserEmail, tvUserPhone;
    private TextView tvTotalSessions, tvTotalVehicles, tvCurrentLanguage;
    private CardView cardLanguage;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userManager = AppState.getInstance().getUserManager();

        bindViews();
        setupListeners();
        loadUserData();
    }

    private void bindViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnEdit = findViewById(R.id.btnEdit);
        
        tvInitials = findViewById(R.id.tvInitials);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvTotalSessions = findViewById(R.id.tvTotalSessions);
        tvTotalVehicles = findViewById(R.id.tvTotalVehicles);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        
        cardLanguage = findViewById(R.id.cardLanguage);
        btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Edit profile coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListeners() {
        cardLanguage.setOnClickListener(v -> showLanguageDialog());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadUserData() {
        User user = userManager.getCurrentUser();
        if (user != null) {
            tvInitials.setText(user.getInitials());
            tvUserName.setText(user.getFullName());
            tvUserEmail.setText(user.getEmail());
            tvUserPhone.setText(user.getPhoneNumber());
        }

        int historyCount = AppState.getInstance().getHistoryManager().getHistoryCount();
        int vehicleCount = AppState.getInstance().getVehicleManager().getVehicleCount();
        
        tvTotalSessions.setText(String.valueOf(historyCount));
        tvTotalVehicles.setText(String.valueOf(vehicleCount));

        String currentLang = LanguageHelper.getCurrentLanguage(this);
        String langName = "English";
        if (currentLang.equals("fr")) langName = "Français";
        else if (currentLang.equals("ar")) langName = "العربية";
        tvCurrentLanguage.setText(langName);
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Français", "العربية"};
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.language_selection))
            .setItems(languages, (dialog, which) -> {
                String selectedLang = "en";
                if (which == 1) selectedLang = "fr";
                else if (which == 2) selectedLang = "ar";
                
                LanguageHelper.setLocale(this, selectedLang);
                recreate();
            })
            .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_logout_title))
            .setMessage(getString(R.string.dialog_logout_msg))
            .setPositiveButton(getString(R.string.btn_logout), (dialog, which) -> logout())
            .setNegativeButton(getString(R.string.dialog_cancel), null)
            .show();
    }

    private void logout() {
        // Clear user data
        userManager.logout();
        UserPreferences.clearUser(this);
        
        // Navigate to welcome
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}