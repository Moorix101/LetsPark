package com.moorixlabs.park;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.moorixlabs.park.models.VehicleManager;
import com.moorixlabs.park.models.Vehicle;
import com.moorixlabs.park.utils.LanguageHelper;

/**
 * AddVehicleActivity - Enhanced UI for vehicle creation
 */
public class AddVehicleActivity extends AppCompatActivity {

    private VehicleManager vehicleManager;
    private TextInputEditText etName, etPlate;
    private ChipGroup chipGroupType;
    private GridLayout colorGrid;
    private Button btnSave;
    private TextView toolbarTitle;
    
    // Preview Elements
    private TextView previewName, previewPlate;
    private android.view.View previewIconBg;

    private String editingVehicleId = null;
    private String selectedColor = "#000000";
    private String selectedType = "Car";

    private static final String[] VEHICLE_TYPES = {
            "Car", "Motorcycle", "SUV", "Truck", "Van", "Electric"
    };

    private static final String[] QUICK_COLORS = {
            "#000000", "#FFFFFF", "#EF4444", "#3B82F6", "#10B981", "#F59E0B",
            "#8B4513", "#6B7280", "#EC4899", "#8B5CF6", "#14B8A6", "#F97316"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        vehicleManager = AppState.getInstance().getVehicleManager();

        // Check if editing
        editingVehicleId = getIntent().getStringExtra("vehicleId");

        bindViews();
        setupChips();
        setupColorGrid();
        setupListeners();

        if (editingVehicleId != null) {
            loadVehicleData();
        } else {
             toolbarTitle.setText(R.string.title_add_vehicle);
             btnSave.setText(R.string.btn_save_vehicle);
        }
        
        updatePreview();
    }

    private void bindViews() {
        toolbarTitle = findViewById(R.id.toolbarTitle);
        etName = findViewById(R.id.etName);
        etPlate = findViewById(R.id.etPlate);
        chipGroupType = findViewById(R.id.chipGroupType);
        colorGrid = findViewById(R.id.colorGrid);
        btnSave = findViewById(R.id.btnSave);
        ImageButton btnBack = findViewById(R.id.btnBack);
        
        previewName = findViewById(R.id.previewName);
        previewPlate = findViewById(R.id.previewPlate);
        previewIconBg = findViewById(R.id.previewIconBg);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupChips() {
        for (String type : VEHICLE_TYPES) {
            Chip chip = new Chip(this);
            chip.setText(type);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setCheckedIconVisible(true);
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F3F4F6")));
            
            if (type.equals(selectedType)) {
                chip.setChecked(true);
            }
            
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedType = type;
                    // Reset styling for others handled by ChipGroup singleSelection
                }
            });
            
            chipGroupType.addView(chip);
        }
    }

    private void setupColorGrid() {
        for (String color : QUICK_COLORS) {
            // Using a container to create a border effect since CardView.setStroke is only available in newer Material components or XML
            CardView container = new CardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 130;
            params.height = 130;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            container.setLayoutParams(params);
            container.setRadius(65);
            container.setCardElevation(0);
            
            // Inner color card
            CardView innerCard = new CardView(this);
            android.widget.FrameLayout.LayoutParams innerParams = new android.widget.FrameLayout.LayoutParams(
                110, 110
            );
            innerParams.gravity = android.view.Gravity.CENTER;
            innerCard.setLayoutParams(innerParams);
            innerCard.setRadius(55);
            innerCard.setCardBackgroundColor(Color.parseColor(color));
            innerCard.setCardElevation(4);
            
            // Selection logic
            if (color.equals(selectedColor)) {
                container.setCardBackgroundColor(Color.parseColor("#10B981")); // Green ring
            } else {
                container.setCardBackgroundColor(Color.TRANSPARENT);
            }

            container.addView(innerCard);
            
            container.setOnClickListener(v -> {
                selectedColor = color;
                colorGrid.removeAllViews();
                setupColorGrid();
                updatePreview();
            });

            colorGrid.addView(container);
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveVehicle());

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview(); }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        
        etName.addTextChangedListener(watcher);
        etPlate.addTextChangedListener(watcher);
    }

    private void updatePreview() {
        String name = etName.getText().toString();
        String plate = etPlate.getText().toString();
        
        previewName.setText(name.isEmpty() ? "New Vehicle" : name);
        previewPlate.setText(plate.isEmpty() ? "ABC-123" : plate);
        
        try {
            int color = Color.parseColor(selectedColor);
            android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
            shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            shape.setCornerRadius(32f);
            shape.setColor(color);
            previewIconBg.setBackground(shape);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVehicleData() {
        toolbarTitle.setText(R.string.title_edit_vehicle);
        btnSave.setText(R.string.btn_update_vehicle);

        Vehicle vehicle = vehicleManager.getVehicleById(editingVehicleId);
        if (vehicle != null) {
            etName.setText(vehicle.getName());
            etPlate.setText(vehicle.getPlateNumber());
            selectedColor = vehicle.getColor();
            selectedType = vehicle.getType();
            
            // Update Chips
            chipGroupType.removeAllViews();
            setupChips();
            
            // Update Color Grid
            colorGrid.removeAllViews();
            setupColorGrid();
            
            updatePreview();
        }
    }

    private void saveVehicle() {
        String name = etName.getText().toString().trim();
        String plate = etPlate.getText().toString().trim();
        
        if (name.isEmpty() || plate.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingVehicleId != null) {
            Vehicle updatedVehicle = new Vehicle(name, selectedType, plate, selectedColor);
            vehicleManager.updateVehicle(editingVehicleId, updatedVehicle);
            Toast.makeText(this, getString(R.string.msg_vehicle_updated), Toast.LENGTH_SHORT).show();
        } else {
            Vehicle newVehicle = new Vehicle(name, selectedType, plate, selectedColor);
            vehicleManager.addVehicle(newVehicle);
            Toast.makeText(this, getString(R.string.msg_vehicle_added), Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}