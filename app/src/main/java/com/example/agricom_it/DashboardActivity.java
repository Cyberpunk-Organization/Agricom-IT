package com.example.agricom_it;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.agricom_it.databinding.ActivityDashboardBinding;
import com.example.agricom_it.ui.HomeFragment;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
