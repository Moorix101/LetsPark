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
import com.moorixlabs.park.utils.LanguageHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * SessionActivity - Minimal bridge for live session tracking
 */
public class SessionActivity extends AppCompatActivity {

    private static final int PAYMENT_REQUEST_CODE = 1001;

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
        // Load Locale
        LanguageHelper.loadLocale(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        initializeManagers();

        if (!sessionController.hasActiveSession()) {
            Toast.makeText(this, getString(R.string.err_no_active_session), Toast.LENGTH_SHORT).show();
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
        
        // Update Labels from Resources
        ((TextView)findViewById(R.id.label_session_active_title)).setText(R.string.label_session_active);
        ((TextView)findViewById(R.id.label_current_cost)).setText(R.string.label_current_cost);
        ((TextView)findViewById(R.id.label_session_details)).setText(R.string.label_session_details);
        ((TextView)findViewById(R.id.label_parking_spot)).setText(R.string.label_parking_spot);
        ((TextView)findViewById(R.id.label_vehicle)).setText(R.string.label_vehicle);
        ((TextView)findViewById(R.id.label_start_time)).setText(R.string.label_start_time);
        btnFinishSession.setText(R.string.btn_finish_session);
        ((TextView)findViewById(R.id.tvHeaderTitle)).setText(R.string.label_active_session);
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
            String currency = getString(R.string.currency_symbol);
            costText.setText(CostCalculator.formatCost(cost, currency));
        }
    }

    private void showFinishDialog() {
        double finalCost = parkingManager.getCurrentCost();
        String currency = getString(R.string.currency_symbol);
        String formattedCost = CostCalculator.formatCost(finalCost, currency);
        
        String msg = String.format(getString(R.string.dialog_finish_session_msg), formattedCost);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_finish_session_title))
                .setMessage(msg)
                .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> startPayment(finalCost))
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .show();
    }
    
    private void startPayment(double amount) {
        ParkingSession session = sessionController.getActiveSession();
        ParkingSpot spot = sessionController.getActiveSessionSpot();
        
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("sessionId", session.getSessionId());
        intent.putExtra("duration", System.currentTimeMillis() - session.getStartTime());
        intent.putExtra("spotLabel", spot.getLabel());
        startActivityForResult(intent, PAYMENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            finishSession();
        }
    }

    private void finishSession() {
        SessionController.SessionEndResult result = sessionController.endSession();

        if (result.isSuccess()) {
            Toast.makeText(this, getString(R.string.msg_session_completed), Toast.LENGTH_SHORT).show();
            goHome();
        } else {
            String message = getLocalizedMessage(result.getMessageKey());
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    private String getLocalizedMessage(String key) {
        int resId = getResources().getIdentifier(key, "string", getPackageName());
        if (resId != 0) {
            return getString(resId);
        }
        return key;
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