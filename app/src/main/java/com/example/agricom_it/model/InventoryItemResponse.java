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

    //-----------------------------------------------------------------------------------[isSuccess]
    public boolean isSuccess()
    {
        return success;
    }

    //----------------------------------------------------------------------------------[setSuccess]
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    //-------------------------------------------------------------------------------------[getData]
    public InventoryItem getData()
    {
        return data;
    }

    //-------------------------------------------------------------------------------------[setData]
    public void setData(InventoryItem data)
    {
        this.data = data;
    }

    //------------------------------------------------------------------------------------[getError]
    public String getError()
    {
        return error;
    }

    //------------------------------------------------------------------------------------[setError]
    public void setError(String error)
    {
        this.error = error;
    }

    //------------------------------------------------------------------------------------[toString]
    @Override
    public String toString() {
        return "InventoryItemResponse{" +
                "success=" + success +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
