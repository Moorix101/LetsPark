package com.moorixlabs.park;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
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
    private Button btnPay;
    
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
        setupListeners();
    }

    private void bindViews() {
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvSpot = findViewById(R.id.tvSpot);
        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);
        btnPay = findViewById(R.id.btnPay);
        ImageButton btnBack = findViewById(R.id.btnBack);

        tvDuration.setText(TimeCalculator.formatDuration(duration));
        tvSpot.setText(spotLabel);
        
        String currency = getString(R.string.currency_symbol);
        tvTotalAmount.setText(CostCalculator.formatCost(amount, currency));
        
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            btnPay.setEnabled(true);
        });

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        int checkedId = rgPaymentMethods.getCheckedRadioButtonId();
        
        if (checkedId == R.id.rbCash) {
            showCashPaymentDialog();
        } else if (checkedId == R.id.rbCard) {
            processCardPayment();
        }
    }

    private void showCashPaymentDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.dialog_cash_title));
        dialog.setMessage(getString(R.string.dialog_cash_msg)); 
        dialog.setCancelable(false);
        dialog.show();

        // Simulate Inspector Confirmation (4 seconds)
        new Handler().postDelayed(() -> {
            if (!isFinishing()) {
                dialog.dismiss();
                completePayment(Payment.PaymentMethod.CASH, R.string.msg_cash_success);
            }
        }, 4000);
    }

    private void processCardPayment() {
        // Show standard loading
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.msg_processing_payment));
        dialog.setCancelable(false);
        dialog.show();

        // Simulate API (1.5 seconds)
        new Handler().postDelayed(() -> {
            if (!isFinishing()) {
                dialog.dismiss();
                completePayment(Payment.PaymentMethod.CARD, 0); 
            }
        }, 1500);
    }

    private void completePayment(Payment.PaymentMethod method, int customSuccessMsgId) {
        Payment payment = new Payment(sessionId, amount, method);
        PaymentManager.PaymentResult result = paymentManager.processPayment(payment);
        
        if (result.success) {
            String msg = (customSuccessMsgId != 0) ? getString(customSuccessMsgId) : getLocalizedMessage(result.messageKey);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, getLocalizedMessage(result.messageKey), Toast.LENGTH_LONG).show();
        }
    }
    
    private String getLocalizedMessage(String key) {
        int resId = getResources().getIdentifier(key, "string", getPackageName());
        if (resId != 0) {
            return getString(resId);
        }
        return key;
    }
}