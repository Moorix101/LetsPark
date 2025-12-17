package com.moorixlabs.park;


import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.moorixlabs.park.models.VehicleManager;
import com.moorixlabs.park.models.Vehicle;

/**
 * AddVehicleActivity - Minimal bridge for vehicle creation/editing
 */
public class AddVehicleActivity extends AppCompatActivity {

    private VehicleManager vehicleManager;
    private EditText etName, etPlate, etColor;
    private Spinner spinnerType;
    private android.view.View colorPreview;
    private Button btnSave;
    private TextView toolbarTitle;

    private String editingVehicleId = null;
    private String selectedColor = "#000000";

    private static final String[] VEHICLE_TYPES = {
            "Car", "Motorcycle", "SUV", "Truck", "Van", "Electric Car"
    };

    private static final String[] QUICK_COLORS = {
            "#000000", "#FFFFFF", "#EF4444", "#3B82F6", "#10B981", "#F59E0B",
            "#8B4513", "#6B7280", "#EC4899", "#8B5CF6", "#14B8A6", "#F97316"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        vehicleManager = AppState.getInstance().getVehicleManager();

        // Check if editing
        editingVehicleId = getIntent().getStringExtra("vehicleId");

        bindViews();
        setupSpinner();
        setupColorGrid();
        setupListeners();

        if (editingVehicleId != null) {
            loadVehicleData();
        }
    }

    private void bindViews() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        etName = findViewById(R.id.etName);
        etPlate = findViewById(R.id.etPlate);
        etColor = findViewById(R.id.etColor);
        spinnerType = findViewById(R.id.spinnerType);
        colorPreview = findViewById(R.id.colorPreview);
        btnSave = findViewById(R.id.btnSave);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                VEHICLE_TYPES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void setupColorGrid() {
        GridLayout grid = findViewById(R.id.colorGrid);

        for (String color : QUICK_COLORS) {
            CardView card = new CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            card.setLayoutParams(params);
            card.setRadius(8);
            card.setCardBackgroundColor(Color.parseColor(color));
            card.setUseCompatPadding(true);

            android.view.View inner = new android.view.View(this);
            inner.setLayoutParams(new android.view.ViewGroup.LayoutParams(60, 60));
            card.addView(inner);

            card.setOnClickListener(v -> {
                selectedColor = color;
                etColor.setText(color);
                updateColorPreview();
            });

            grid.addView(card);
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveVehicle());

        etColor.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedColor = s.toString();
                updateColorPreview();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void updateColorPreview() {
        try {
            colorPreview.setBackgroundColor(Color.parseColor(selectedColor));
        } catch (Exception e) {
            colorPreview.setBackgroundColor(Color.BLACK);
        }
    }

    private void loadVehicleData() {
        toolbarTitle.setText("Edit Vehicle");
        btnSave.setText("Update Vehicle");

        Vehicle vehicle = vehicleManager.getVehicleById(editingVehicleId);
        if (vehicle != null) {
            etName.setText(vehicle.getName());
            etPlate.setText(vehicle.getPlateNumber());
            etColor.setText(vehicle.getColor());

            for (int i = 0; i < VEHICLE_TYPES.length; i++) {
                if (VEHICLE_TYPES[i].equals(vehicle.getType())) {
                    spinnerType.setSelection(i);
                    break;
                }
            }

            selectedColor = vehicle.getColor();
            updateColorPreview();
        }
    }

    private void saveVehicle() {
        String name = etName.getText().toString().trim();
        String plate = etPlate.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String color = selectedColor;

        if (name.isEmpty() || plate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingVehicleId != null) {
            // Update existing vehicle
            Vehicle updatedVehicle = new Vehicle(name, type, plate, color);
            vehicleManager.updateVehicle(editingVehicleId, updatedVehicle);
            Toast.makeText(this, "Vehicle updated!", Toast.LENGTH_SHORT).show();
        } else {
            // Add new vehicle
            Vehicle newVehicle = new Vehicle(name, type, plate, color);
            vehicleManager.addVehicle(newVehicle);
            Toast.makeText(this, "Vehicle added!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}