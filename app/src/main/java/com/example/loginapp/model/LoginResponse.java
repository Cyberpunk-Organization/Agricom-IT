package com.example.loginapp.model;

public class LoginResponse {
    private String message;
    private String token;
    private int userId;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }
}
