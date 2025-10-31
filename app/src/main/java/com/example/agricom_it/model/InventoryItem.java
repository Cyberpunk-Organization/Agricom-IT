package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

public class InventoryItem {
    private static final long serialVersionUID = 1L;

    @SerializedName("success")
    private boolean success;
    private String name;
    //    private String category;
    private int count;
    @SerializedName("error")
    private String error;
    @SerializedName("info")
    private String info;
    @SerializedName("data")
    private String data;

    private int itemId = -1;
    private int inventoryItemId = -1;


    public InventoryItem(String name, int count)
    {
        this.name = name;
        this.count = count;
    }

    public InventoryItem(String name, int count, int itemId, int inventoryItemId) {
        this.name = name;
        this.count = count;
        this.itemId = itemId;
        this.inventoryItemId = inventoryItemId;
    }

    public String getName() { return name; }
    public int getCount() { return count; }

    public Boolean getSuccess(){ return success;  }

    public String getData() { return data; }

    public int getItemID() { return itemId; }

    public void setItemID(int itemID) { this.itemId = itemID; }

    public void setCount(int newQuantity) { this.count = newQuantity; }

    public int getInventoryID() { return inventoryItemId; }

    public void setInventoryID(int inventoryItemID) { this.inventoryItemId = inventoryItemID; }
}
