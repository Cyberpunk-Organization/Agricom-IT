package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class ItemResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("success")
    private boolean success;
    @SerializedName( "data")
    private InventoryItem data; // matches sendJson(true, $row)
    @SerializedName("error")
    private String error;
    @SerializedName("info")
    private Map<String, Object> info;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public InventoryItem getData() { return data; }
    public void setData(InventoryItem data) { this.data = data; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public Map<String, Object> getInfo() { return info; }
    public void setInfo(Map<String, Object> info) { this.info = info; }

    @Override
    public String toString() {
        return "ItemResponse{success=" + success + ", data=" + data + ", error=" + error + ", info=" + info + "}";
    }
}