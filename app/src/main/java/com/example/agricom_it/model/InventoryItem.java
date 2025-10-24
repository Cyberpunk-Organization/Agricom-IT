package com.example.agricom_it.model;

public class InventoryItem {
    private String name;
    private String category;
    private int count;

    public InventoryItem(String name, String category, int count) {
        this.name = name;
        this.category = category;
        this.count = count;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getCount() { return count; }
}
