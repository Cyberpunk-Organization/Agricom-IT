package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("ok")
    private String ok;
    @SerializedName("error")
    private String error;

    //-----------------------------------------------------------------------------------[getStatus]
    public String getStatus() {
        return ok;
    }

    //-----------------------------------------------------------------------------------[setStatus]
    public void setStatus(String status) {
        this.ok = ok;
    }

    //----------------------------------------------------------------------------------[getMessage]
    public String getMessage() {
        return error;
    }

    //----------------------------------------------------------------------------------[setMessage]
    public void setMessage(String message) {
        this.error = error;
    }
}
