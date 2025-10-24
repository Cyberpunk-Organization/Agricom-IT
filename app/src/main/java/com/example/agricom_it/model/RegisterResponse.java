package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents the JSON response received from the server after a registration attempt.
 * It typically includes a status and a message to inform the client of the outcome.
 */
public class RegisterResponse {

    @SerializedName("ok")
    private String ok;

    @SerializedName("error")
    private String error;

    // Getters
    public String getStatus() {
        return ok;
    }

    public String getMessage() {
        return error;
    }

    // Setters (optional, but good practice)
    public void setStatus(String status) {
        this.ok = ok;
    }

    public void setMessage(String message) {
        this.error = error;
    }
}
