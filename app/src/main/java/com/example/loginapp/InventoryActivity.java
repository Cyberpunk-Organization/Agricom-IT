package com.example.loginapp;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    InventoryAdapter adapter;
    List<InventoryItem> inventoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.rvInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        inventoryList = new ArrayList<>();
        inventoryList.add(new InventoryItem("Wheat", "Crop", 120));
        inventoryList.add(new InventoryItem("Corn", "Crop", 85));
        inventoryList.add(new InventoryItem("Cows", "Livestock", 20));
        inventoryList.add(new InventoryItem("Sheep", "Livestock", 45));

        adapter = new InventoryAdapter(inventoryList);
        recyclerView.setAdapter(adapter);
    }
}
