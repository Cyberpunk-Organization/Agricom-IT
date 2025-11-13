package com.example.agricom_it;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.agricom_it.activity.DashboardActivity;
import com.example.agricom_it.activity.RegisterActivity;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.LoginRequest;
import com.example.agricom_it.model.LoginResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private TextView register_link;
    private final String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    //------------------------------------------------------------------------------------[onCreate]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);
        register_link = findViewById(R.id.register_link);

        loginBtn.setOnClickListener(v -> loginUser());
        
        register_link.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();
        });

        try
        {
            FirebaseApp.initializeApp(this);
            Log.i(TAG, "Firebase initialized successfully.");
        }
        catch(Exception e)
        {
            Log.e(TAG, "Error initializing Firebase: ", e);
        }
    }

    //-----------------------------------------------------------------------------------[loginUser]
    private void loginUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(username, password);
        AuthApiService apiService = ApiClient.getService();

        Call<LoginResponse> call = apiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    LoginResponse loginResponse = response.body();

                    String message = loginResponse.getMessage();
                    if (message == null || message.trim().isEmpty()) {
                        message = "Login successful. Welcome!";
                    }

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    if ("true".equalsIgnoreCase(loginResponse.getStatus()))
                    {
//                        Toast.makeText(MainActivity.this, "Login successful. Welcome!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);

                        LoginResponse lr = loginResponse;
                        intent.putExtra("login_id", lr.getID());

                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Login failed with code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Response Code:" + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }
}