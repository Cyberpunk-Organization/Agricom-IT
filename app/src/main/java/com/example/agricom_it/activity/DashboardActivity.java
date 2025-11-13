package com.example.agricom_it.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.example.agricom_it.databinding.ActivityDashboardBinding;
import com.example.agricom_it.ui.HomeFragment;
import android.content.Intent;


public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: DashboardActivity started");

        Intent intent = getIntent();
        int userID = -1;

        if ( intent != null && intent.hasExtra("login_id"))
        {
//            Log.d(TAG, "Intent has login_id extra: " + intent.getIntExtra("login_id", -1));
            userID = intent.getIntExtra("login_id", -1);
            Log.d(TAG, "Received userID: " + userID);
        }
        else
        {
            Log.e(TAG, "No userID found in Intent");
        }


        // Inflate layout using view binding
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.contentFrame.getId(), new HomeFragment())
                    .commit();
        }
    }
}
