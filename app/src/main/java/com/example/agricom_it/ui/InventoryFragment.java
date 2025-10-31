package com.example.agricom_it.ui;

import android.app.AlertDialog;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.InventoryAdapter;
import com.example.agricom_it.R;
import com.example.agricom_it.api.ApiResponse;
import com.example.agricom_it.model.InventoryItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.agricom_it.model.ItemResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;
    private Button btnAddItem, btnSort;
    private boolean sortAscending = true;

    private final AuthApiService apiService = ApiClient.getService();

    private final String TAG = "InventoryFragment";
    private int userID = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

//        Bundle args = getArguments();

//        Log.d(TAG, "Arguments received: " + args);

//        if ( args != null && args.containsKey("userID") )
//        {
//            Log.d(TAG, "Received userID: " + args.getInt("userID"));
//            userID = args.getInt("userID", -1);
//        }






        recyclerView = view.findViewById(R.id.rvInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddItem =view.findViewById(R.id.btn_add_item);
        btnSort = view.findViewById(R.id.btn_sort_toggle);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        inventoryList = new ArrayList<>();

        adapter = new InventoryAdapter(inventoryList);
        recyclerView.setAdapter(adapter);

        btnAddItem.setOnClickListener(v -> addNewItem());

        btnSort.setOnClickListener(v -> toggleSortByName());

        return view;
    }

    // Add Item Functionality
    private void addNewItem() {
        // Inflate a simple dialog layout (you can create it or use AlertDialog builder)
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView)
                .setTitle("Add New Inventory Item")
                .setPositiveButton("Add", null)   // we override later
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        EditText etName     = dialogView.findViewById(R.id.et_item_name);
        EditText etQuantity = dialogView.findViewById(R.id.et_item_quantity);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override positive button after show() so we can validate
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();

            if (name.isEmpty())
            {
                etName.setError("Name required");
                return;
            }
            int quantity = 1;
            if (!qtyStr.isEmpty())
            {
                try { quantity = Integer.parseInt(qtyStr); }
                catch (NumberFormatException e)
                {
                    etQuantity.setError("Invalid number");
                    return;
                }
            }
            if (quantity <= 0)
            {
                etQuantity.setError("Quantity > 0");
                return;
            }

            // ---- create the item -------------------------------------------------
//            InventoryItem newItem = new InventoryItem(name, quantity);
//            inventoryList.add(newItem);
//            adapter.notifyItemInserted(inventoryList.size() - 1);






            Toast.makeText(requireContext(), "Item added!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
    private void toggleSortByName() {
        sortAscending = !sortAscending;     // flip

        Comparator<InventoryItem> comparator = Comparator.comparing(InventoryItem::getName);
        if (!sortAscending) {
            comparator = comparator.reversed();
        }

        Collections.sort(inventoryList, comparator);
        adapter.notifyDataSetChanged();

        // Update button text
        Button btnSort = requireView().findViewById(R.id.btn_sort_toggle);
        btnSort.setText(sortAscending ? "Sort: A to Z" : "Sort: Z to A");

        Toast.makeText(requireContext(),
                "Sorted " + (sortAscending ? "A to Z" : "Z to A"),
                Toast.LENGTH_SHORT).show();
    }
}
