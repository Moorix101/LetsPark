package com.moorixlabs.park;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.moorixlabs.park.models.SessionController;
import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingManager;
import com.moorixlabs.park.models.ParkingSession;
import com.moorixlabs.park.models.ParkingSpot;
import com.moorixlabs.park.models.Vehicle;
import com.moorixlabs.park.models.CostCalculator;
import com.moorixlabs.park.models.TimeCalculator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * SessionActivity - Minimal bridge for live session tracking
 */
public class SessionActivity extends AppCompatActivity {

    private ParkingManager parkingManager;
    private SessionController sessionController;

    private TextView timerText;
    private TextView costText;
    private TextView spotText;
    private TextView vehicleText;
    private TextView startTimeText;
    private Button btnFinishSession;

    private Handler handler;
    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        initializeManagers();

        if (!sessionController.hasActiveSession()) {
            Toast.makeText(this, "No active session", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        setupListeners();
        displaySessionInfo();
        startTimer();
    }

    private void initializeManagers() {
        parkingManager = AppState.getInstance().getParkingManager();
        sessionController = new SessionController(
                parkingManager,
                AppState.getInstance().getVehicleManager(),
                AppState.getInstance().getHistoryManager()
        );
    }

    private void bindViews() {
        timerText = findViewById(R.id.timerText);
        costText = findViewById(R.id.costText);
        spotText = findViewById(R.id.spotText);
        vehicleText = findViewById(R.id.vehicleText);
        startTimeText = findViewById(R.id.startTimeText);
        btnFinishSession = findViewById(R.id.btnFinishSession);
        ImageButton btnHome = findViewById(R.id.btnHome);

        btnHome.setOnClickListener(v -> goHome());
    }

    private void setupListeners() {
        btnFinishSession.setOnClickListener(v -> showFinishDialog());
    }

    private void displaySessionInfo() {
        ParkingSession session = sessionController.getActiveSession();
        Vehicle vehicle = sessionController.getActiveSessionVehicle();
        ParkingSpot spot = sessionController.getActiveSessionSpot();

        if (session != null && vehicle != null && spot != null) {
            spotText.setText(spot.getLabel());
            vehicleText.setText(vehicle.getName());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            startTimeText.setText(sdf.format(new Date(session.getStartTime())));
        }
    }

    private void startTimer() {
        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateDisplay();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateRunnable);
    }

    private void updateDisplay() {
        ParkingSession session = sessionController.getActiveSession();
        if (session != null) {
            long elapsed = System.currentTimeMillis() - session.getStartTime();
            timerText.setText(TimeCalculator.formatDuration(elapsed));

            double cost = parkingManager.getCurrentCost();
            costText.setText(CostCalculator.formatCost(cost));
        }
    }

    private void showFinishDialog() {
        double finalCost = parkingManager.getCurrentCost();

        new AlertDialog.Builder(this)
                .setTitle("Finish Session")
                .setMessage("Total cost: " + CostCalculator.formatCost(finalCost) + "\n\nConfirm payment?")
                .setPositiveButton("Confirm", (dialog, which) -> finishSession())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void finishSession() {
        SessionController.SessionEndResult result = sessionController.endSession();

        if (result.isSuccess()) {
            Toast.makeText(this, "Session completed!", Toast.LENGTH_SHORT).show();
            goHome();
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }
}