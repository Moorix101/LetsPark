package com.moorixlabs.park;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.moorixlabs.park.models.SessionController;
import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingManager;
import com.moorixlabs.park.models.VehicleManager;
import com.moorixlabs.park.models.UserManager;
import com.moorixlabs.park.models.ParkingSession;
import com.moorixlabs.park.models.CostCalculator;
import com.moorixlabs.park.utils.LanguageHelper;

public class MainActivity extends AppCompatActivity {

    private ParkingManager parkingManager;
    private VehicleManager vehicleManager;
    private HistoryManager historyManager;
    private UserManager userManager;
    private SessionController sessionController;

    private TextView availableSpotsText;
    private TextView hourlyRateText;
    private CardView activeSessionCard;
    private TextView activeSessionInfo;
    private Button btnStartParking;
    private CardView btnVehicles;
    private CardView btnHistory;
    private ImageButton btnProfile;
    private TextView tvUserGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeManagers();
        bindViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }

    private void initializeManagers() {
        parkingManager = AppState.getInstance().getParkingManager();
        vehicleManager = AppState.getInstance().getVehicleManager();
        historyManager = AppState.getInstance().getHistoryManager();
        userManager = AppState.getInstance().getUserManager();
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
        btnProfile = findViewById(R.id.btnProfile);
        tvUserGreeting = findViewById(R.id.tvUserName);
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
        
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void updateDashboard() {
        // Get data from pure Java layer
        int available = parkingManager.getAvailableSpots();
        int total = parkingManager.getTotalSpots();
        double rate = parkingManager.getHourlyRate();
        boolean hasSession = sessionController.hasActiveSession();

        // Update UI
        availableSpotsText.setText(available + " / " + total);
        
        String currencySymbol = getString(R.string.currency_symbol);
        hourlyRateText.setText((int)rate + " " + currencySymbol + "/h");

        // Update greeting with user name
        tvUserGreeting.setText(userManager.getGreeting());

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