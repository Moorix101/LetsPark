package com.moorixlabs.park;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moorixlabs.park.models.VehicleManager;
import com.moorixlabs.park.models.Vehicle;
import java.util.List;

/**
 * VehiclesActivity - Minimal bridge for vehicle management
 */
public class VehiclesActivity extends AppCompatActivity {

    private VehicleManager vehicleManager;
    private RecyclerView vehiclesList;
    private LinearLayout emptyState;
    private VehicleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);

        vehicleManager = AppState.getInstance().getVehicleManager();

        bindViews();
        setupListeners();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay();
    }

    private void bindViews() {
        vehiclesList = findViewById(R.id.vehiclesList);
        emptyState = findViewById(R.id.emptyState);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddVehicle);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVehicleActivity.class);
            startActivity(intent);
        });
    }

    private void setupListeners() {
        // FAB click handled in bindViews
    }

    private void setupRecyclerView() {
        adapter = new VehicleAdapter();
        vehiclesList.setLayoutManager(new LinearLayoutManager(this));
        vehiclesList.setAdapter(adapter);
    }

    private void updateDisplay() {
        List<Vehicle> vehicles = vehicleManager.getAllVehicles();

        if (vehicles.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            vehiclesList.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            vehiclesList.setVisibility(View.VISIBLE);
            adapter.updateVehicles(vehicles);
        }
    }

    private class VehicleAdapter extends RecyclerView.Adapter<VehicleViewHolder> {
        private List<Vehicle> vehicles;

        @NonNull
        @Override
        public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_vehicle, parent, false);
            return new VehicleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
            holder.bind(vehicles.get(position));
        }

        @Override
        public int getItemCount() {
            return vehicles != null ? vehicles.size() : 0;
        }

        public void updateVehicles(List<Vehicle> newVehicles) {
            this.vehicles = newVehicles;
            notifyDataSetChanged();
        }
    }

    private class VehicleViewHolder extends RecyclerView.ViewHolder {
        TextView name, type, plate, color, defaultBadge;
        View colorIndicator;
        ImageButton btnMenu;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vehicleName);
            type = itemView.findViewById(R.id.vehicleType);
            plate = itemView.findViewById(R.id.vehiclePlate);
            color = itemView.findViewById(R.id.vehicleColor);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            defaultBadge = itemView.findViewById(R.id.defaultBadge);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }

        public void bind(Vehicle vehicle) {
            name.setText(vehicle.getName());
            type.setText(vehicle.getType());
            plate.setText(vehicle.getPlateNumber());
            color.setText(vehicle.getColor());

            try {
                colorIndicator.setBackgroundColor(Color.parseColor(vehicle.getColor()));
            } catch (Exception e) {
                colorIndicator.setBackgroundColor(Color.BLACK);
            }

            defaultBadge.setVisibility(vehicle.isDefault() ? View.VISIBLE : View.GONE);

            btnMenu.setOnClickListener(v -> showMenu(v, vehicle));
        }

        private void showMenu(View anchor, Vehicle vehicle) {
            PopupMenu popup = new PopupMenu(VehiclesActivity.this, anchor);
            popup.getMenu().add("Make Default");
            popup.getMenu().add("Edit");
            popup.getMenu().add("Delete");

            popup.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                switch (title) {
                    case "Make Default":
                        vehicleManager.setDefaultVehicle(vehicle.getId());
                        updateDisplay();
                        return true;
                    case "Edit":
                        Intent intent = new Intent(VehiclesActivity.this, AddVehicleActivity.class);
                        intent.putExtra("vehicleId", vehicle.getId());
                        startActivity(intent);
                        return true;
                    case "Delete":
                        vehicleManager.deleteVehicle(vehicle.getId());
                        updateDisplay();
                        return true;
                }
                return false;
            });
            popup.show();
        }
    }
}