package com.moorixlabs.park;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.moorixlabs.park.models.SessionController;
import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingManager;
import com.moorixlabs.park.models.VehicleManager;
import com.moorixlabs.park.models.ParkingSpot;
import com.moorixlabs.park.utils.LanguageHelper;

import java.util.List;

/**
 * SpotActivity - Minimal bridge for spot selection
 */
public class SpotActivity extends AppCompatActivity {

    private ParkingManager parkingManager;
    private VehicleManager vehicleManager;
    private HistoryManager historyManager;
    private SessionController sessionController;

    private GridLayout leftSpotsContainer;
    private GridLayout rightSpotsContainer;
    private Button btnStartSession;
    private String selectedSpotId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
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
        leftSpotsContainer = findViewById(R.id.leftSpotsContainer);
        rightSpotsContainer = findViewById(R.id.rightSpotsContainer);
        btnStartSession = findViewById(R.id.BtnStartSession);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        
        // Update Labels from Resources
        ((TextView)findViewById(R.id.tvTitle)).setText(R.string.title_select_spot);
        ((TextView)findViewById(R.id.tvLegendFree)).setText(R.string.legend_free);
        ((TextView)findViewById(R.id.tvLegendTaken)).setText(R.string.legend_taken);
        ((TextView)findViewById(R.id.tvLegendSelected)).setText(R.string.legend_selected);
        btnStartSession.setText(R.string.btn_start_session);
    }

    private void setupListeners() {
        btnStartSession.setOnClickListener(v -> startParkingSession());
    }

    private void loadSpotGrid() {
        leftSpotsContainer.removeAllViews();
        rightSpotsContainer.removeAllViews();
        List<ParkingSpot> spots = parkingManager.getAllSpots();

        // Split spots: 50% left, 50% right
        int midPoint = (int) Math.ceil(spots.size() / 2.0);

        for (int i = 0; i < spots.size(); i++) {
            ParkingSpot spot = spots.get(i);
            CardView card = createSpotCard(spot);
            
            if (i < midPoint) {
                leftSpotsContainer.addView(card);
            } else {
                rightSpotsContainer.addView(card);
            }
        }
    }

    private CardView createSpotCard(ParkingSpot spot) {
        CardView card = new CardView(this);

        // Set layout params for Grid items (1 column)
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 300; // Fixed width or match_parent equivalent
        params.height = 180; // Taller spots
        params.setMargins(16, 24, 16, 24); // More separation vertically
        // Note: GridLayout.spec is needed if we want specific row/col placement, 
        // but auto-placement works if we just add views. 
        // However, defining width is tricky in code without strict context. 
        // Let's set a reasonable fixed size for "realistic" look.
        
        card.setLayoutParams(params);

        // Card styling
        card.setRadius(12);
        card.setCardElevation(0);
        card.setUseCompatPadding(false); // We handle margins manually

        // Text view for label
        android.widget.TextView text = new android.widget.TextView(this);
        text.setText(spot.getLabel());
        text.setTextSize(18);
        text.setTextColor(Color.WHITE);
        text.setGravity(Gravity.CENTER);
        text.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);

        // Apply Figma Status Colors
        if (spot.isOccupied()) {
            card.setCardBackgroundColor(Color.parseColor("#ef4444")); // Red
            card.setAlpha(0.6f);
            card.setClickable(false);
        } else {
            card.setCardBackgroundColor(Color.parseColor("#22c55e")); // Green
            card.setClickable(true);
            card.setOnClickListener(v -> selectSpot(spot, card));
        }

        card.addView(text);
        return card;
    }

    private void selectSpot(ParkingSpot spot, CardView card) {
        // Reset all cards in LEFT container
        resetContainerCards(leftSpotsContainer);
        // Reset all cards in RIGHT container
        resetContainerCards(rightSpotsContainer);

        // Highlight selected
        card.setCardBackgroundColor(Color.parseColor("#3B82F6")); // Blue
        selectedSpotId = spot.getSpotId();
        btnStartSession.setEnabled(true);
    }

    private void resetContainerCards(GridLayout container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            android.view.View child = container.getChildAt(i);
            if (child instanceof CardView) {
                CardView c = (CardView) child;
                // We need to check if the spot associated with this card is free.
                // Since we don't have a direct map from View -> Spot here easily 
                // without tagging, let's rely on the color check or re-fetch.
                // A better way is to set tag on create.
                
                // Hacky check: if alpha is 0.6, it's occupied (Red). Don't touch it.
                if (c.getAlpha() < 0.9f) continue;

                c.setCardBackgroundColor(Color.parseColor("#22c55e")); // Reset to Green
            }
        }
    }

    private void startParkingSession() {
        if (selectedSpotId == null) {
            Toast.makeText(this, getString(R.string.msg_select_spot), Toast.LENGTH_SHORT).show();
            return;
        }

        SessionController.SessionStartResult result = sessionController.startSession(selectedSpotId);

        if (result.isSuccess()) {
            Intent intent = new Intent(this, SessionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            // Translate the error key to a localized message
            String message = getLocalizedMessage(result.getMessageKey());
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
    
    private String getLocalizedMessage(String key) {
        int resId = getResources().getIdentifier(key, "string", getPackageName());
        if (resId != 0) {
            return getString(resId);
        }
        return key; // Fallback to key if not found
    }
}