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

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
        super.onCreate(savedInstanceState);
        
        initializeApp();
    }

    private void initializeApp() {
        User savedUser = UserPreferences.loadUser(this);
        
        if (savedUser != null) {
            AppState.getInstance().getUserManager().setCurrentUser(savedUser);
            navigateToMain();
        } else {
            showWelcomeScreen();
        }
    }

    private void showWelcomeScreen() {
        setContentView(R.layout.activity_welcome);
        
        Button btnGetStarted = findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(v -> navigateToCreateAccount());

        ImageButton btnLanguage = findViewById(R.id.btnLanguage);
        btnLanguage.setOnClickListener(v -> {
            LanguageHelper.showLanguageDialog(this);
        });
    }

    private void navigateToMain() {
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