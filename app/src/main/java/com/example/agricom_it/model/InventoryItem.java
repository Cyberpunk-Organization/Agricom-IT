package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

public class InventoryItem {
    private static final long serialVersionUID = 1L;
    @SerializedName("success")
    private boolean success;
    private String name;
    private int count;
    @SerializedName("error")
    private String error;
    @SerializedName("info")
    private String info;
    @SerializedName("data")
    private String data;
    private int itemId = -1;
    private int inventoryItemId = -1;

    //-------------------------------------------------------------------------------[InventoryItem]
    public InventoryItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    //-------------------------------------------------------------------------------[InventoryItem]
    public InventoryItem(String name, int count, int itemId, int inventoryItemId) {
        this.name = name;
        this.count = count;
        this.itemId = itemId;
        this.inventoryItemId = inventoryItemId;
    }

    //-------------------------------------------------------------------------------------[getName]
    public String getName() { return name; }

    //------------------------------------------------------------------------------------[getCount]
    public int getCount() { return count; }

    //------------------------------------------------------------------------------------[setCount]
    public void setCount(int newQuantity) { this.count = newQuantity; }

    //----------------------------------------------------------------------------------[getSuccess]
    public Boolean getSuccess(){ return success;  }

    //-------------------------------------------------------------------------------------[getData]
    public String getData() { return data; }

    //-----------------------------------------------------------------------------------[getItemID]
    public int getItemID() { return itemId; }

    //-----------------------------------------------------------------------------------[setItemID]
    public void setItemID(int itemID) { this.itemId = itemID; }

    //------------------------------------------------------------------------------[getInventoryID]
    public int getInventoryID() { return inventoryItemId; }

    //------------------------------------------------------------------------------[setInventoryID]
    public void setInventoryID(int inventoryItemID) { this.inventoryItemId = inventoryItemID; }
}
