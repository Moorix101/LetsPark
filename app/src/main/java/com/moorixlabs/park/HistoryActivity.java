package com.moorixlabs.park;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.moorixlabs.park.models.HistoryManager;
import com.moorixlabs.park.models.ParkingHistory;
import com.moorixlabs.park.models.CostCalculator;
import com.moorixlabs.park.utils.LanguageHelper;

import java.util.List;

/**
 * HistoryActivity - Minimal bridge for history display
 */
public class HistoryActivity extends AppCompatActivity {

    private HistoryManager historyManager;
    private RecyclerView historyList;
    private LinearLayout emptyState;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyManager = AppState.getInstance().getHistoryManager();

        bindViews();
        setupRecyclerView();
        updateDisplay();
    }

    private void bindViews() {
        historyList = findViewById(R.id.historyList);
        emptyState = findViewById(R.id.emptyState);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        
        ((TextView)findViewById(R.id.tvTitle)).setText(R.string.title_history);
        ((TextView)findViewById(R.id.tvNoHistory)).setText(R.string.msg_no_history);
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter();
        historyList.setLayoutManager(new LinearLayoutManager(this));
        historyList.setAdapter(adapter);
    }

    private void updateDisplay() {
        List<ParkingHistory> history = historyManager.getAllHistory();

        if (history.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            historyList.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            historyList.setVisibility(View.VISIBLE);
            adapter.updateHistory(history);
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {
        private List<ParkingHistory> historyList;

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            holder.bind(historyList.get(position));
        }

        @Override
        public int getItemCount() {
            return historyList != null ? historyList.size() : 0;
        }

        public void updateHistory(List<ParkingHistory> newHistory) {
            this.historyList = newHistory;
            notifyDataSetChanged();
        }
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, costText, timeText, durationText, spotText, vehicleText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            costText = itemView.findViewById(R.id.costText);
            timeText = itemView.findViewById(R.id.timeText);
            durationText = itemView.findViewById(R.id.durationText);
            spotText = itemView.findViewById(R.id.spotText);
            vehicleText = itemView.findViewById(R.id.vehicleText);
        }

        public void bind(ParkingHistory history) {
            dateText.setText(history.getFormattedDate());
            
            // Fix formatting to include currency symbol
            String currency = itemView.getContext().getString(R.string.currency_symbol);
            costText.setText(CostCalculator.formatCost(history.getSession().getTotalCost(), currency));
            
            timeText.setText(history.getFormattedStartTime() + " - " + history.getFormattedEndTime());
            durationText.setText(history.getFormattedDuration());
            spotText.setText(history.getSpot().getLabel());
            vehicleText.setText(history.getVehicle().getName());
        }
    }
}