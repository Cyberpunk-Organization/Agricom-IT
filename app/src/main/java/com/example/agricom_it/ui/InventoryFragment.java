package com.example.agricom_it.ui;

import android.app.AlertDialog;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryList;
    private Button btnAddItem, btnSort;
    private boolean sortAscending = true;

    private final AuthApiService apiService = ApiClient.getService();

    private final String TAG = "InventoryFragment";
    private int userID = -1;

    private interface NameCallback { void onName(String name); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey("userID"))
        {
            userID = args.getInt("userID", -1);
        }

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

        if (userID >= 0)
        {
            Log.d(TAG, "Loading inventory for userID: " + userID);
            loadInventoryForUser(userID);
        }
        return view;
    }

    private void loadInventoryForUser(int userId) {
        // Request the inventory content rows for this user
        Call<ResponseBody> call = apiService.getInventoryItems("GetItemsFromUserID", userId);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                if (!response.isSuccessful() || response.body() == null)
                {
                    Toast.makeText(requireContext(), "Failed to load inventory", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "inventory response unsuccessful: " + response.code());
                    return;
                }
                try
                {
                    String json = response.body().string();
                    Gson gson = new GsonBuilder().create();
                    Log.d(TAG, "Inventory JSON: " + json);

                    JsonObject root = gson.fromJson(json, JsonObject.class);
                    if (root == null) {
                        Log.e(TAG, "inventory JSON root null");
                        return;
                    }

                    boolean success = root.has("success") && root.get("success").getAsBoolean();
                    if (!success) {
                        String error = root.has("error") ? root.get("error").getAsString() : "unknown";
                        Log.e(TAG, "inventory API returned error: " + error);
                        Toast.makeText(requireContext(), "Server error loading inventory", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JsonElement dataElem = root.get("data");
                    if (dataElem == null || dataElem.isJsonNull()) {
                        // no items
                        Log.d(TAG, "no inventory items for user " + userId);
                        return;
                    }

                    // Aggregate quantities per ItemID (in case same ItemID appears multiple times)
                    Map<Integer, Integer> qtyByItemId = new HashMap<>();
                    if (dataElem.isJsonArray()) {
                        JsonArray arr = dataElem.getAsJsonArray();
                        for (JsonElement el : arr) {
                            if (!el.isJsonObject()) continue;
                            JsonObject row = el.getAsJsonObject();
                            if (!row.has("ItemID")) continue;
                            int itemId = row.get("ItemID").getAsInt();
                            int qty = 1;
                            if (row.has("Quantity") && !row.get("Quantity").isJsonNull()) {
                                try { qty = row.get("Quantity").getAsInt(); } catch (Exception ex) { qty = 1; }
                            }
                            qtyByItemId.put(itemId, qtyByItemId.getOrDefault(itemId, 0) + qty);
                        }
                    } else {
                        Log.w(TAG, "unexpected data type for inventory items");
                        return;
                    }

                    // Clear existing list and fetch item details for each distinct ItemID
                    inventoryList.clear();
                    adapter.notifyDataSetChanged();

                    for (Map.Entry<Integer, Integer> e : qtyByItemId.entrySet()) {
                        int itemId = e.getKey();
                        int totalQty = e.getValue();
                        fetchItemDetails(itemId, totalQty);
                    }

                } catch (IOException | JsonSyntaxException e) {
                    Log.e(TAG, "Error processing inventory response", e);
                    Toast.makeText(requireContext(), "Error processing inventory response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "inventory request failed", t);
                Toast.makeText(requireContext(), "Network error loading inventory", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchItemDetails(int itemId, int quantity) {
        Call<ResponseBody> call = apiService.getItem("GetItem", itemId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e(TAG, "getItem unsuccessful for id " + itemId + " code:" + (response != null ? response.code() : "null"));
                    return;
                }
                try {
                    String json = response.body().string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject root = gson.fromJson(json, JsonObject.class);
                    if (root == null) return;

                    boolean success = root.has("success") && root.get("success").getAsBoolean();
                    if (!success) {
                        Log.w(TAG, "getItem returned success=false for id " + itemId);
                    }

                    JsonElement dataElem = root.get("data");
                    String name = "Unknown";
                    if (dataElem != null && dataElem.isJsonObject()) {
                        JsonObject dataObj = dataElem.getAsJsonObject();
                        if (dataObj.has("Name") && !dataObj.get("Name").isJsonNull()) {
                            name = dataObj.get("Name").getAsString();
                        } else if (dataObj.has("name") && !dataObj.get("name").isJsonNull()) {
                            name = dataObj.get("name").getAsString();
                        }
                    } else {
                        // if data null, server may have returned info found=false
                        Log.d(TAG, "getItem data null for id " + itemId);
                    }

                    final String finalName = name;
                    requireActivity().runOnUiThread(() -> {
                        // create InventoryItem using existing constructor used elsewhere
                        // earlier code showed InventoryItem(String name, int quantity) usage
                        InventoryItem newItem = new InventoryItem(finalName, quantity);
                        inventoryList.add(newItem);
                        adapter.notifyItemInserted(inventoryList.size() - 1);
                    });

                } catch (IOException | JsonSyntaxException ex) {
                    Log.e(TAG, "Error parsing getItem response for id " + itemId, ex);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "getItem request failed for id " + itemId, t);
            }
        });
    }

    // Add Item Functionality
    private void addNewItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView)
                .setTitle("Add New Inventory Item")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss());

        EditText etName = dialogView.findViewById(R.id.et_item_name);
        EditText etQuantity = dialogView.findViewById(R.id.et_item_quantity);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String qtyStr = etQuantity.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name required");
                return;
            }
            int quantity = 1;
            if (!qtyStr.isEmpty()) {
                try { quantity = Integer.parseInt(qtyStr); }
                catch (NumberFormatException e) {
                    etQuantity.setError("Invalid number");
                    return;
                }
            }
            if (quantity <= 0) {
                etQuantity.setError("Quantity > 0");
                return;
            }

            // 1) Create the inventory item (AddItem)
            Call<ResponseBody> addItemCall = apiService.AddItem("AddItem", name);
            int finalQuantity = quantity;
            int finalQuantity1 = quantity;
            addItemCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Failed to create item", Toast.LENGTH_SHORT).show());
                        return;
                    }
                    try {
                        String json = response.body().string();
                        Gson gson = new GsonBuilder().create();
                        JsonObject root = gson.fromJson(json, JsonObject.class);
                        if (root == null) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Bad server response", Toast.LENGTH_SHORT).show());
                            return;
                        }
                        boolean success = root.has("success") && root.get("success").getAsBoolean();
                        if (!success) {
                            String err = root.has("error") ? root.get("error").getAsString() : "server error";
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "AddItem error: " + err, Toast.LENGTH_SHORT).show());
                            return;
                        }

                        JsonElement dataElem = root.get("data");
                        int itemID;
                        String returnedName;
                        if (dataElem != null && dataElem.isJsonObject()) {
                            JsonObject data = dataElem.getAsJsonObject();
                            if (data.has("ItemID") && !data.get("ItemID").isJsonNull()) {
                                itemID = data.get("ItemID").getAsInt();
                            } else {
                                itemID = -1;
                            }
                            if (data.has("Name") && !data.get("Name").isJsonNull()) {
                                returnedName = data.get("Name").getAsString();
                            } else {
                                returnedName = name;
                            }
                        } else {
                            returnedName = name;
                            itemID = -1;
                        }

                        if (itemID <= 0) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Invalid ItemID from server", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        // 2) Get InventoryID for this user
                        Map<String, Integer> params = new HashMap<>();
                        params.put("userID", userID);
                        Call<ResponseBody> getInvCall = apiService.inventory("GetInventoryIDByUserID", params);
                        getInvCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (!response.isSuccessful() || response.body() == null) {
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(requireContext(), "Failed to get inventory ID", Toast.LENGTH_SHORT).show());
                                    return;
                                }
                                try {
                                    String invJson = response.body().string();
                                    Gson gson = new GsonBuilder().create();
                                    JsonObject invRoot = gson.fromJson(invJson, JsonObject.class);
                                    if (invRoot == null) {
                                        requireActivity().runOnUiThread(() ->
                                                Toast.makeText(requireContext(), "Bad inventory response", Toast.LENGTH_SHORT).show());
                                        return;
                                    }
                                    boolean invSuccess = invRoot.has("success") && invRoot.get("success").getAsBoolean();
                                    if (!invSuccess) {
                                        requireActivity().runOnUiThread(() ->
                                                Toast.makeText(requireContext(), "Inventory API error", Toast.LENGTH_SHORT).show());
                                        return;
                                    }

                                    Integer inventoryID = null;
                                    JsonElement invData = invRoot.get("data");
                                    if (invData != null && invData.isJsonObject()) {
                                        JsonObject invObj = invData.getAsJsonObject();
                                        if (invObj.has("InventoryID") && !invObj.get("InventoryID").isJsonNull()) {
                                            inventoryID = invObj.get("InventoryID").getAsInt();
                                        }
                                    }

                                    // If inventoryID missing, create inventory (AddInventory)
                                    if (inventoryID == null) {
                                        Map<String, Integer> addInvParams = new HashMap<>();
                                        addInvParams.put("UserID", userID); // note capitalized per PHP AddInventory
                                        Call<ResponseBody> addInvCall = apiService.inventory("AddInventory", addInvParams);
                                        addInvCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (!response.isSuccessful() || response.body() == null) {
                                                    requireActivity().runOnUiThread(() ->
                                                            Toast.makeText(requireContext(), "Failed to create inventory", Toast.LENGTH_SHORT).show());
                                                    return;
                                                }
                                                try {
                                                    String addInvJson = response.body().string();
                                                    JsonObject addInvRoot = new GsonBuilder().create().fromJson(addInvJson, JsonObject.class);
                                                    if (addInvRoot == null || !addInvRoot.has("success") || !addInvRoot.get("success").getAsBoolean()) {
                                                        requireActivity().runOnUiThread(() ->
                                                                Toast.makeText(requireContext(), "AddInventory failed", Toast.LENGTH_SHORT).show());
                                                        return;
                                                    }
                                                    JsonElement addInvData = addInvRoot.get("data");
                                                    Integer newInventoryID = null;
                                                    if (addInvData != null && addInvData.isJsonObject()) {
                                                        JsonObject addInvObj = addInvData.getAsJsonObject();
                                                        if (addInvObj.has("InventoryID") && !addInvObj.get("InventoryID").isJsonNull()) {
                                                            newInventoryID = addInvObj.get("InventoryID").getAsInt();
                                                        }
                                                    }
                                                    if (newInventoryID == null) {
                                                        requireActivity().runOnUiThread(() ->
                                                                Toast.makeText(requireContext(), "Server returned no InventoryID", Toast.LENGTH_SHORT).show());
                                                        return;
                                                    }
                                                    // proceed to add item to inventory
                                                    addItemToInventoryAndShow(newInventoryID, itemID, finalQuantity, returnedName, dialog);
                                                } catch (IOException | JsonSyntaxException ex) {
                                                    Log.e(TAG, "Error parsing AddInventory response", ex);
                                                    requireActivity().runOnUiThread(() ->
                                                            Toast.makeText(requireContext(), "Parse error", Toast.LENGTH_SHORT).show());
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Log.e(TAG, "AddInventory failed", t);
                                                requireActivity().runOnUiThread(() ->
                                                        Toast.makeText(requireContext(), "Network error creating inventory", Toast.LENGTH_SHORT).show());
                                            }
                                        });
                                    } else {
                                        // inventoryID found -> add item to inventory
                                        addItemToInventoryAndShow(inventoryID, itemID, finalQuantity1, returnedName, dialog);
                                    }

                                } catch (IOException | JsonSyntaxException ex) {
                                    Log.e(TAG, "Error parsing GetInventoryID response", ex);
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(requireContext(), "Parse error", Toast.LENGTH_SHORT).show());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e(TAG, "GetInventoryID request failed", t);
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(requireContext(), "Network error getting inventory id", Toast.LENGTH_SHORT).show());
                            }
                        });

                    } catch (IOException | JsonSyntaxException ex) {
                        Log.e(TAG, "Error parsing AddItem response", ex);
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Parse error", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "AddItem request failed", t);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Network error creating item", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    // helper to call AddItemToInventory then update UI
    private void addItemToInventoryAndShow(int inventoryID, int itemID, int quantity, String name, AlertDialog dialog) {
        Call<ResponseBody> addToInv = apiService.addItemToInventory("AddItemToInventory", inventoryID, itemID, quantity);
        addToInv.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Failed to add item to inventory", Toast.LENGTH_SHORT).show());
                    return;
                }
                try {
                    String json = response.body().string();
                    JsonObject root = new GsonBuilder().create().fromJson(json, JsonObject.class);
                    if (root == null || !root.has("success") || !root.get("success").getAsBoolean()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Server error adding to inventory", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    requireActivity().runOnUiThread(() -> {
                        InventoryItem newItem = new InventoryItem(name, quantity);
                        inventoryList.add(newItem);
                        adapter.notifyItemInserted(inventoryList.size() - 1);
                        Toast.makeText(requireContext(), "Item added!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });

                } catch (IOException | JsonSyntaxException ex) {
                    Log.e(TAG, "Error parsing AddItemToInventory response", ex);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Parse error", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "AddItemToInventory failed", t);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Network error adding to inventory", Toast.LENGTH_SHORT).show());
            }
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


