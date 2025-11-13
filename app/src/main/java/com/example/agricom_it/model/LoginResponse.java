package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @SerializedName("error")
    private String error;
    @SerializedName("token")
    private String token;
    @SerializedName("ok")
    private String ok;
    @SerializedName("id")
    private int id;

    //----------------------------------------------------------------------------------[getMessage]
    public String getMessage() {
        return error;
    }

    //------------------------------------------------------------------------------------[getToken]
    public String getToken() {
        return token;
    }

    //-----------------------------------------------------------------------------------[getStatus]
    public String getStatus() {
        return ok;
    }

    //---------------------------------------------------------------------------------------[getID]
    public int getID() { return id; }
}
