package com.example.loginapp.model;

public class LoginRequest {
    private String Username;
    private String Password;

    public LoginRequest(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
    }

    // Getters
    public String getUsername() {
        return  Username;
    }

    public String getPassword() {
        return Password;
    }
}
