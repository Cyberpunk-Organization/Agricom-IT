package com.example.loginapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_chats:
                    Toast.makeText(this, "Chat clicked", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_tasks:
                    Toast.makeText(this, "Tasks clicked", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_settings:
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;


        }
    });

}