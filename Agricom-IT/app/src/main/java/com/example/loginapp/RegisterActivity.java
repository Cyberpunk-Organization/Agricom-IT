package com.example.agricom_it;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerBtn;
    private TextView tvBackToLogin;
    private Spinner roleSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerBtn = findViewById(R.id.register_btn);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        roleSpinner = findViewById(R.id.role_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_roles,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        registerBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

}