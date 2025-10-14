package com.example.loginapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.databinding.ActivityDashboardBinding;
import com.example.loginapp.fragments.BottomNavFragment;
import com.example.loginapp.ui.ChatFragment;
import com.example.loginapp.ui.HomeFragment;
import com.example.loginapp.ui.InventoryFragment;
import com.example.loginapp.ui.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        BottomNavFragment bottomNavFragment = (BottomNavFragment) getSupportFragmentManager()
                .findFragmentById(R.id.bottomNavFragment);

        if (bottomNavFragment != null && bottomNavFragment.getView() != null) {
            BottomNavigationView bottomNav = bottomNavFragment.getView().findViewById(R.id.bottom_nav);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentFrame, new HomeFragment())
                            .commit();
                    return true;
                } else if (id == R.id.nav_chat) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentFrame, new ChatFragment())
                            .commit();
                    return true;
                } else if (id == R.id.nav_inventory) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentFrame, new InventoryFragment())
                            .commit();
                    return true;
                } else if (id == R.id.nav_settings) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.contentFrame, new SettingsFragment())
                            .commit();
                    return true;
                }

                return false;
            });
        }
    }
}
