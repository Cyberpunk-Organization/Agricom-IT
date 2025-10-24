package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("error")
    private String error;
    @SerializedName("token")
    private String token;
    @SerializedName("userId")
    private int userId;

    @SerializedName("ok")
    private String ok;

    public String getMessage() {
        return error;
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public String getStatus() {
        return ok;
    }
}
