package com.moorixlabs.park;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.cardview.widget.CardView;
import com.moorixlabs.park.models.SessionController;
import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingManager;
import com.moorixlabs.park.models.VehicleManager;
import com.moorixlabs.park.models.ParkingSession;
import com.moorixlabs.park.models.CostCalculator;
import com.moorixlabs.park.utils.LanguageHelper;

/**
 * MainActivity - Minimal bridge between UI and Pure Java logic
 */
public class MainActivity extends AppCompatActivity {

    // Pure Java managers (99% of logic)
    private ParkingManager parkingManager;
    private VehicleManager vehicleManager;
    private HistoryManager historyManager;
    private SessionController sessionController;

    // UI elements (1% of code)
    private TextView availableSpotsText;
    private TextView hourlyRateText;
    private CardView activeSessionCard;
    private TextView activeSessionInfo;
    private Button btnStartParking;
    private CardView btnVehicles; // Changed from Button to CardView
    private CardView btnHistory;  // Changed from Button to CardView
    private ImageButton btnLanguageSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved language before content view
        LanguageHelper.loadLocale(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize pure Java managers
        initializeManagers();

        // Bind UI elements
        bindViews();

        // Set click listeners
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update UI with latest data from pure Java layer
        updateDashboard();
    }

    private void initializeManagers() {
        parkingManager = AppState.getInstance().getParkingManager();
        vehicleManager = AppState.getInstance().getVehicleManager();
        historyManager = AppState.getInstance().getHistoryManager();
        sessionController = new SessionController(parkingManager, vehicleManager, historyManager);
    }

    private void bindViews() {
        availableSpotsText = findViewById(R.id.availableSpots);
        hourlyRateText = findViewById(R.id.hourlyRate);
        activeSessionCard = findViewById(R.id.activeSessionCard);
        activeSessionInfo = findViewById(R.id.activeSessionInfo);
        btnStartParking = findViewById(R.id.btnStartParking);
        btnVehicles = findViewById(R.id.btnVehicles);
        btnHistory = findViewById(R.id.btnHistory);
        btnLanguageSwitch = findViewById(R.id.btnLanguageSwitch);
    }

    private void setupListeners() {
        btnStartParking.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SpotActivity.class);
            startActivity(intent);
        });

        btnVehicles.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VehiclesActivity.class);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        activeSessionCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SessionActivity.class);
            startActivity(intent);
        });
        
        btnLanguageSwitch.setOnClickListener(v -> showLanguageDialog());
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

    private void updateDashboard() {
        // Get data from pure Java layer
        int available = parkingManager.getAvailableSpots();
        int total = parkingManager.getTotalSpots();
        double rate = parkingManager.getHourlyRate();
        boolean hasSession = sessionController.hasActiveSession();

        // Update UI
        availableSpotsText.setText(available + " / " + total);
        
        // Use localized string for formatting
        String currencySymbol = getString(R.string.currency_symbol);
        hourlyRateText.setText((int)rate + " " + currencySymbol + "/h");

        if (hasSession) {
            activeSessionCard.setVisibility(View.VISIBLE);
            ParkingSession session = sessionController.getActiveSession();
            String spotLabel = parkingManager.getSpotById(session.getSpotId()).getLabel();
            String infoText = getString(R.string.active_session_info_template, spotLabel);
            activeSessionInfo.setText(infoText);
        } else {
            activeSessionCard.setVisibility(View.GONE);
        }
    }
}