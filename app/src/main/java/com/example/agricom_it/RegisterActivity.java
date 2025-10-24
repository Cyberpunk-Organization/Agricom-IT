package com.example.agricom_it;

import android.app.ProgressDialog;
import android.content.Intent;
// import android.content.SharedPreferences; // No longer needed here
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.api.ApiClient;
// No longer need LoginResponse, User, or Gson here
import com.example.agricom_it.model.RegisterRequest;
import com.example.agricom_it.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, surnameInput, usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerBtn;
    private TextView tvBackToLogin;
    private Spinner roleSpinner;

    private ProgressDialog progressDialog;

    // These are not needed in RegisterActivity anymore
    // private static final String PREFS_NAME = "app_prefs";
    // private static final String PREFS_USER_KEY = "user";
    // private static final String PREFS_TOKEN_KEY = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.name_input);
        surnameInput = findViewById(R.id.surname_input);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering...");

        registerBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String surname = surnameInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            String role = roleSpinner.getSelectedItem() != null ? roleSpinner.getSelectedItem().toString() : "";


            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

//            RegisterRequest request = new RegisterRequest(name, surname, username, email, password, role);
            RegisterRequest request = new RegisterRequest(name, surname, username, email, password);

            registerWithRetrofit(request);
        });

        tvBackToLogin.setOnClickListener(v -> {
            // This should go to the login screen, which is MainActivity
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    private void registerWithRetrofit(RegisterRequest request) {
        progressDialog.show();

        AuthApiService apiService = ApiClient.getService();
        Call<RegisterResponse> call = apiService.register(request);

        // Corrected Callback to use RegisterResponse
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    // Display the message from the server (e.g., "Registration successful")
                    Toast.makeText(RegisterActivity.this, registerResponse.getMessage(), Toast.LENGTH_LONG).show();

                    // Check the status from the response
                    if ("success".equalsIgnoreCase(registerResponse.getStatus())) {
                        // On success, redirect the user to the login screen to sign in
                        Toast.makeText(RegisterActivity.this, "Registration successful. Please log in.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish(); // Finish RegisterActivity so the user can't go back to it
                    } else {
                        // Handle cases where registration was not successful but the server responded
                        // For example, if the username or email is already taken
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle unsuccessful responses (e.g., 404, 500)
                    Toast.makeText(RegisterActivity.this, "Registration failed with code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                // Handle network errors or other failures
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
