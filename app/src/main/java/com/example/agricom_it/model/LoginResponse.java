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


    public String getMessage() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public String getStatus() {
        return ok;
    }

    public int getID()
    {
        return id;
//        return user != null ? user.getUserID() : -1;
    }

}
