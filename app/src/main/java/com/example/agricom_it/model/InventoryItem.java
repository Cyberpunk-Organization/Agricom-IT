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


    public InventoryItem(String name, int count)
    {
        this.name = name;
        this.count = count;
    }

    public String getName()
    {
//        return name;
        return name;
    }
////    public String getCategory() { return category; }
    public int getCount()
    {
        return count;
//        return -1;
    }

    public Boolean getSuccess(){ return success;  }

    public String getData()
    {
        return data;
    }
}
