package com.moorixlabs.park;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.moorixlabs.park.models.SessionController;
import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingManager;
import com.moorixlabs.park.models.VehicleManager;
import com.moorixlabs.park.models.ParkingSpot;
import java.util.List;

/**
 * SpotActivity - Minimal bridge for spot selection
 */
public class SpotActivity extends AppCompatActivity {

    private ParkingManager parkingManager;
    private VehicleManager vehicleManager;
    private HistoryManager historyManager;
    private SessionController sessionController;

    private GridLayout spotGrid;
    private Button btnStartSession;
    private String selectedSpotId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);

        initializeManagers();
        bindViews();
        setupListeners();
        loadSpotGrid();
    }

    private void initializeManagers() {
        parkingManager = AppState.getInstance().getParkingManager();
        vehicleManager = AppState.getInstance().getVehicleManager();
        historyManager = AppState.getInstance().getHistoryManager();
        sessionController = new SessionController(parkingManager, vehicleManager, historyManager);
    }

    private void bindViews() {
        spotGrid = findViewById(R.id.SpotGrid);
        btnStartSession = findViewById(R.id.BtnStartSession);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnStartSession.setOnClickListener(v -> startParkingSession());
    }

    private void loadSpotGrid() {
        spotGrid.removeAllViews();
        List<ParkingSpot> spots = parkingManager.getAllSpots();

        for (ParkingSpot spot : spots) {
            CardView card = createSpotCard(spot);
            spotGrid.addView(card);
        }
    }

    private CardView createSpotCard(ParkingSpot spot) {
        CardView card = new CardView(this);

        // Set layout params
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        card.setLayoutParams(params);

        // Card styling
        card.setRadius(12);
        card.setCardElevation(4);
        card.setUseCompatPadding(true);

        // Text view for label
        android.widget.TextView text = new android.widget.TextView(this);
        text.setText(spot.getLabel());
        text.setTextSize(18);
        text.setTextColor(Color.WHITE);
        text.setGravity(Gravity.CENTER);
        text.setPadding(0, 60, 0, 60);
        text.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);

        // Set color based on status
        if (spot.isOccupied()) {
            card.setCardBackgroundColor(Color.parseColor("#EF4444")); // Red
            card.setClickable(false);
        } else {
            card.setCardBackgroundColor(Color.parseColor("#10B981")); // Green
            card.setClickable(true);
            card.setOnClickListener(v -> selectSpot(spot, card));
        }

        card.addView(text);
        return card;
    }

    private void selectSpot(ParkingSpot spot, CardView card) {
        // Reset all cards to green
        for (int i = 0; i < spotGrid.getChildCount(); i++) {
            android.view.View child = spotGrid.getChildAt(i);
            if (child instanceof CardView) {
                CardView c = (CardView) child;
                ParkingSpot s = parkingManager.getAllSpots().get(i);
                if (s.isFree()) {
                    c.setCardBackgroundColor(Color.parseColor("#10B981"));
                }
            }
        }

        // Highlight selected
        card.setCardBackgroundColor(Color.parseColor("#3B82F6")); // Blue
        selectedSpotId = spot.getSpotId();
        btnStartSession.setEnabled(true);
    }

    private void startParkingSession() {
        if (selectedSpotId == null) {
            Toast.makeText(this, "Please select a spot", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use pure Java controller
        SessionController.SessionStartResult result = sessionController.startSession(selectedSpotId);

        if (result.isSuccess()) {
            Intent intent = new Intent(this, SessionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}