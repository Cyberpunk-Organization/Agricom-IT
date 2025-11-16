package com.example.agricom_it.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.agricom_it.MainActivity;
import com.example.agricom_it.R;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.RegisterRequest;
import com.example.agricom_it.model.RegisterResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameInput, surnameInput, usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button registerBtn;
    private TextView tvBackToLogin;
    private Spinner roleSpinner;
    private final String TAG = "RegisterActivity_mine";
    private ProgressDialog progressDialog;

    //------------------------------------------------------------------------------------[onCreate]
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
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item); // dropdown rows
        roleSpinner.setAdapter(adapter);

        Drawable popupBg = ContextCompat.getDrawable(this, R.drawable.spinner_popup_frame);
        if (popupBg != null) {
            roleSpinner.setPopupBackgroundDrawable(popupBg);
        }

        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (uiMode == Configuration.UI_MODE_NIGHT_YES) {
            roleSpinner.setPopupBackgroundResource(R.color.black);
        } else {
            roleSpinner.setPopupBackgroundResource(R.color.white);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering...");

        registerBtn.setOnClickListener(v ->
        {
            String name = nameInput.getText().toString().trim();
            String surname = surnameInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            String role = roleSpinner.getSelectedItem() != null ? roleSpinner.getSelectedItem().toString() : "";

            if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterRequest request = new RegisterRequest(name, surname, username, email, password);
            registerWithRetrofit(request);
        });

        tvBackToLogin.setOnClickListener(v ->
        {
            // This should go to the login screen, which is MainActivity
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    //------------------------------------------------------------------------[registerWithRetrofit]
    private void registerWithRetrofit(RegisterRequest request) {
        progressDialog.show();

        AuthApiService apiService = ApiClient.getService();
        Call<RegisterResponse> call = apiService.register(request);

        // Corrected Callback to use RegisterResponse
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();

                    // Display the message from the server (e.g., "Registration successful")
                    String message = registerResponse.getMessage();
                    if (message == null || message.trim().isEmpty()) {
                        message = "Registration successful!";
                    }
                } else {
                    // Handle unsuccessful responses (e.g., 404, 500)
                    progressDialog.dismiss();
                    Log.e(TAG, "Registration failed with code: " + response.code());
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
