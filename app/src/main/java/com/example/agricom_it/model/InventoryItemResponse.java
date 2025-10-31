package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InventoryItemResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
    @SerializedName("success")
    private boolean success;
    @SerializedName( "data")
    private InventoryItem data;
    @SerializedName("error")
    private String error;

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public InventoryItem getData()
    {
        return data;
    }

    public void setData(InventoryItem data)
    {
        this.data = data;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    @Override
    public String toString() {
        return "InventoryItemResponse{" +
                "success=" + success +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
