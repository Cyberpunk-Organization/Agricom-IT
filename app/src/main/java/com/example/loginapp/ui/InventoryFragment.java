package com.example.loginapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapp.InventoryAdapter;
import com.example.loginapp.R;
import com.example.loginapp.databinding.FragmentInventoryBinding;
import com.example.loginapp.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        recyclerView = view.findViewById(R.id.rvInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        inventoryList = new ArrayList<>();
        inventoryList.add(new InventoryItem("Wheat", "Crop", 120));
        inventoryList.add(new InventoryItem("Corn", "Crop", 85));
        inventoryList.add(new InventoryItem("Cows", "Livestock", 20));
        inventoryList.add(new InventoryItem("Sheep", "Livestock", 45));

        adapter = new InventoryAdapter(inventoryList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}

