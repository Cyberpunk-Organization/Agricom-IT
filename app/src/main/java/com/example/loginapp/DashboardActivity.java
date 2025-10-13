package com.example.loginapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.databinding.ActivityDashboardBinding;
import com.example.loginapp.ui.HomeFragment;

public class DashboardActivity extends AppCompatActivity {
    ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load HomeFragment by default into contentFrame
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.contentFrame.getId(), new HomeFragment())
                    .commit();
        }
    }
}
