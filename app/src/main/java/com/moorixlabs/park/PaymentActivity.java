package com.moorixlabs.park;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.moorixlabs.park.models.CostCalculator;
import com.moorixlabs.park.models.Payment;
import com.moorixlabs.park.models.PaymentManager;
import com.moorixlabs.park.models.TimeCalculator;
import com.moorixlabs.park.utils.LanguageHelper;

public class PaymentActivity extends AppCompatActivity {

    private PaymentManager paymentManager;
    private RadioGroup rgPaymentMethods;
    private RadioButton rbPrepaid;
    private Button btnPay;
    private TextView tvBalance;
    
    private double amount;
    private String sessionId;
    private long duration;
    private String spotLabel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentManager = AppState.getInstance().getPaymentManager();
        
        // Get data from Intent
        amount = getIntent().getDoubleExtra("amount", 0.0);
        sessionId = getIntent().getStringExtra("sessionId");
        duration = getIntent().getLongExtra("duration", 0);
        spotLabel = getIntent().getStringExtra("spotLabel");

        bindViews();
        setupSummary();
        setupListeners();
    }

    private void bindViews() {
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvSpot = findViewById(R.id.tvSpot);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        
        tvBalance = findViewById(R.id.tvBalance);
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);
        rbPrepaid = findViewById(R.id.rbPrepaid);
        btnPay = findViewById(R.id.btnPay);
        ImageButton btnBack = findViewById(R.id.btnBack);

        tvDuration.setText(TimeCalculator.formatDuration(duration));
        tvSpot.setText(spotLabel);
        
        String currency = getString(R.string.currency_symbol);
        tvTotalAmount.setText(CostCalculator.formatCost(amount, currency));
        
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupSummary() {
        String currency = getString(R.string.currency_symbol);
        tvBalance.setText("Current Balance: " + CostCalculator.formatCost(paymentManager.getBalance(), currency));
    }

    private void setupListeners() {
        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            btnPay.setEnabled(true);
            if (checkedId == R.id.rbPrepaid) {
                tvBalance.setVisibility(View.VISIBLE);
            } else {
                tvBalance.setVisibility(View.GONE);
            }
        });

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        Payment.PaymentMethod method = Payment.PaymentMethod.CASH;
        int checkedId = rgPaymentMethods.getCheckedRadioButtonId();
        
        if (checkedId == R.id.rbCard) method = Payment.PaymentMethod.CARD;
        else if (checkedId == R.id.rbWallet) method = Payment.PaymentMethod.MOBILE_WALLET;
        else if (checkedId == R.id.rbPrepaid) method = Payment.PaymentMethod.PREPAID_BALANCE;

        Payment payment = new Payment(sessionId, amount, method);

        // Show Loading
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.msg_processing_payment));
        dialog.setCancelable(false);
        dialog.show();

        // Simulate API
        new Handler().postDelayed(() -> {
            PaymentManager.PaymentResult result = paymentManager.processPayment(payment);
            dialog.dismiss();
            
            if (result.success) {
                // Success
                Toast.makeText(this, getLocalizedMessage(result.messageKey), Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            } else {
                // Failure
                Toast.makeText(this, getLocalizedMessage(result.messageKey), Toast.LENGTH_LONG).show();
            }
        }, 1500);
    }
    
    private String getLocalizedMessage(String key) {
        int resId = getResources().getIdentifier(key, "string", getPackageName());
        if (resId != 0) {
            return getString(resId);
        }
        return key;
    }
}