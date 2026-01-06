package com.moorixlabs.park;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.moorixlabs.park.models.UserManager;
import com.moorixlabs.park.models.User;
import com.moorixlabs.park.utils.LanguageHelper;
import com.moorixlabs.park.utils.UserPreferences;

public class CreateAccountActivity extends AppCompatActivity {

    private UserManager userManager;
    private TextInputEditText etFullName, etEmail, etPhone;
    private TextView tvInitialsPreview;
    private Button btnCreateAccount;
    private ImageButton btnLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageHelper.loadLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        userManager = AppState.getInstance().getUserManager();

        bindViews();
        setupListeners();
    }

    private void bindViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        tvInitialsPreview = findViewById(R.id.tvInitialsPreview);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnLanguage = findViewById(R.id.btnLanguage);
    }

    private void setupListeners() {
        etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateInitialsPreview(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCreateAccount.setOnClickListener(v -> createAccount());
        
        if (btnLanguage != null) {
            btnLanguage.setOnClickListener(v -> {
                LanguageHelper.showLanguageDialog(this);
            });
        }
    }

    private void updateInitialsPreview(String name) {
        if (name == null || name.trim().isEmpty()) {
            tvInitialsPreview.setText("?");
            return;
        }

        String[] parts = name.trim().split("\\s+");
        String initials;
        if (parts.length == 1) {
            initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            initials = (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        tvInitialsPreview.setText(initials);
    }

    private void createAccount() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullName.isEmpty()) {
            Toast.makeText(this, getString(R.string.err_name_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.err_phone_required), Toast.LENGTH_SHORT).show();
            return;
        }

        userManager.createUser(fullName, email, phone);
        
        User user = userManager.getCurrentUser();
        UserPreferences.saveUser(this, user);
        
        Toast.makeText(this, getString(R.string.msg_account_created), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}