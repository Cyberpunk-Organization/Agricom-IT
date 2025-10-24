package com.example.agricom_it.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.InventoryAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.model.InventoryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;
    private Button btnAddItem, btnSort;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        recyclerView = view.findViewById(R.id.rvInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddItem =view.findViewById(R.id.btnAddItem);
        btnSort = view.findViewById(R.id.btnSort);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        inventoryList = new ArrayList<>();

        adapter = new InventoryAdapter(inventoryList);
        recyclerView.setAdapter(adapter);

        btnAddItem.setOnClickListener(v -> addNewItem());

        btnSort.setOnClickListener(v -> sortItemsByName());

        return view;
    }

    // Add Item Functionality
    private void addNewItem() {

        InventoryItem newItem = new InventoryItem("New Item" + (inventoryList.size() + 1), "Misc", 1);
        inventoryList.add(newItem);
        adapter.notifyItemInserted(inventoryList.size() - 1);

        Toast.makeText(getContext(), "New item added!", Toast.LENGTH_SHORT).show();

    }

    // Sort Button Functionality
    private void sortItemsByName() {
        Collections.sort(inventoryList, Comparator.comparing(InventoryItem::getName));
        adapter.notifyDataSetChanged();

        Toast.makeText(getContext(), "Items sorted by name", Toast.LENGTH_SHORT).show();
    }
}

