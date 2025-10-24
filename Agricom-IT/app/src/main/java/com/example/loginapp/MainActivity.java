package com.example.agricom_it;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.LoginRequest;
import com.example.agricom_it.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {



    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(v -> loginUser());
    }

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
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Toast.makeText(MainActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    Log.i("API_SUCCESS", "Token: " + loginResponse.getToken());
                    Log.i("API_SUCCESS", "User ID: " + loginResponse.getUserId());

                    // TODO: Navigate to DashboardActivity or save token
                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else{
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
