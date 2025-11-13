package com.example.agricom_it.adapter;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.R;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.InventoryItem;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> inventoryList;
    private final Integer finalInventoryID;
    private final AuthApiService apiService = ApiClient.getService();
    private final String TAG = "InventoryAdapter";

    //----------------------------------------------------------------------------[InventoryAdapter]
    public InventoryAdapter(List<InventoryItem> inventoryList, Integer finalInventoryID) {
        this.inventoryList = inventoryList;
        this.finalInventoryID = finalInventoryID;
    }

    //--------------------------------------------------------------------------[onCreateViewHolder]
    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    //----------------------------------------------------------------------------[onBindViewHolder]
    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);
        holder.tvStockName.setText(item.getName());
        holder.tvCount.setText(String.valueOf(item.getCount()));

        holder.itemView.setOnClickListener(v -> {
            showEditDialog(item, position, holder);
        });

    }

    //--------------------------------------------------------------------------------[getItemCount]
    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    /*==========================================================================
                                       NOTE!
    ==========================================================================*/
    /*
    IMPORTANT: Do not touch this function if you want to create/initialize
    inventory items.
    This function is only for updating/removing items from
    the inventory.

    DO NOT CHANGE THE VARIABLES BELOW.
    */
    //------------------------------------------------------------------------------[showEditDialog]
    private void showEditDialog(InventoryItem item, int position, InventoryViewHolder holder) {
        View dialogView = LayoutInflater.from(holder.itemView.getContext())
                .inflate(R.layout.dialog_edit_inventory_item, null);

        TextView tvItemName = dialogView.findViewById(R.id.tvItemName);
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        Button btnRemove = dialogView.findViewById(R.id.btnRemove);

        tvItemName.setText(item.getName());
        etQuantity.setText(String.valueOf(item.getCount()));

        AlertDialog dialog = new AlertDialog.Builder(holder.itemView.getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnUpdate.setOnClickListener(view -> {
            String qText = etQuantity.getText().toString().trim();
            if (qText.isEmpty()) {
                Toast.makeText(holder.itemView.getContext(), "Enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            int newQuantity;
            try {
                newQuantity = Integer.parseInt(qText);
            } catch (NumberFormatException e) {
                Toast.makeText(holder.itemView.getContext(), "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int inventoryItemId = item.getItemID(); // content id (if used elsewhere)
            if (inventoryItemId < 0) {
                Toast.makeText(holder.itemView.getContext(), "Invalid inventory item id", Toast.LENGTH_SHORT).show();
                return;
            }

            int inventoryID = inventoryList.get(position).getInventoryID(); // inventory id
            // NOTE: server's UpdateQuantity expects InventoryID + ItemID + Quantity.
            Call<ResponseBody> call = apiService.updateItemQuantity("UpdateQuantity", finalInventoryID, item.getItemID(), newQuantity);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        item.setCount(newQuantity);
                        notifyItemChanged(position);
                        Toast.makeText(holder.itemView.getContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String msg = "Update failed";
                        try {
                            ResponseBody body = response.errorBody();
                            if (body != null) msg = body.string();
                        } catch (IOException ignored) {
                        }
                        Toast.makeText(holder.itemView.getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Network error during quantity update", t);
                    Toast.makeText(holder.itemView.getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        btnRemove.setOnClickListener(view -> {
            int itemID = item.getItemID(); // product/item id
            if (itemID < 0) {
                Toast.makeText(holder.itemView.getContext(), "Invalid item id", Toast.LENGTH_SHORT).show();
                return;
            }
            // use adapter's inventoryID (inventory table id) and the item's itemID
            Call<ResponseBody> call = apiService.removeItemFromInventory("RemoveItemFromInventory", finalInventoryID, itemID);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        int removedPos = position;
                        inventoryList.remove(removedPos);
                        notifyItemRemoved(removedPos);
                        Toast.makeText(holder.itemView.getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String msg = "Remove failed";
                        try {
                            ResponseBody body = response.errorBody();
                            if (body != null) msg = body.string();
                        } catch (IOException ignored) {
                            Log.e(TAG, "Error reading error body", ignored);
                        }
                        Toast.makeText(holder.itemView.getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Network error during item removal", t);
                    Toast.makeText(holder.itemView.getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    //-------------------------------------------------------------------------[InventoryViewHolder]
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvStockName, tvCount;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStockName = itemView.findViewById(R.id.tvStockName);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }
}

