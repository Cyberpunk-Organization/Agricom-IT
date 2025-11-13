package com.example.agricom_it.api;

import com.example.agricom_it.model.InventoryItem;

public class ApiResponse {
    public boolean success;
    public InventoryItem itemData;

    //-------------------------------------------------------------------------------[getItemObject]
    public InventoryItem getItemObject() {
        return itemData;
    }
}