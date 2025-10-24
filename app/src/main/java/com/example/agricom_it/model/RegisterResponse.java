package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents the JSON response received from the server after a registration attempt.
 * It typically includes a status and a message to inform the client of the outcome.
 */
public class RegisterResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // Getters
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // Setters (optional, but good practice)
    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
