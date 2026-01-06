package com.moorixlabs.park;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.moorixlabs.park.models.User;
import com.moorixlabs.park.utils.LanguageHelper;
import com.moorixlabs.park.utils.UserPreferences;

/**
 * Welcome/Splash Activity
 * - Loads saved user data
 * - If user exists: auto-navigate to MainActivity
 * - If no user: show welcome screen
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
        super.onCreate(savedInstanceState);
        
        // Initialize app (load saved user)
        initializeApp();
    }

    private void initializeApp() {
        // Load saved user from SharedPreferences
        User savedUser = UserPreferences.loadUser(this);
        
        if (savedUser != null) {
            // User exists - set in manager and navigate to main
            AppState.getInstance().getUserManager().setCurrentUser(savedUser);
            navigateToMain();
        } else {
            // No user - show welcome screen
            showWelcomeScreen();
        }
    }

    private void showWelcomeScreen() {
        setContentView(R.layout.activity_welcome);
        
        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v -> navigateToCreateAccount());

        // Setup language button
        ImageButton btnLanguage = findViewById(R.id.btnLanguage);
        btnLanguage.setOnClickListener(v -> {
            LanguageHelper.showLanguageDialog(this);
        });
    }

    private void navigateToMain() {
        // Small delay for smooth transition
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 500);
    }

    private void navigateToCreateAccount() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
        finish();
    }
}