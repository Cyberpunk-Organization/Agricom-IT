package com.example.loginapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.ui.InventoryFragment;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory); // we'll create this next

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.inventoryContainer, new InventoryFragment())
                    .commit();
        }
    }
}
