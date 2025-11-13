package com.example.agricom_it.model;

public class LoginRequest {
    private String Username;
    private String Password;

    //--------------------------------------------------------------------------------[LoginRequest]
    public LoginRequest(String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
    }

    //---------------------------------------------------------------------------------[getUsername]
    public String getUsername() {
        return Username;
    }

    //---------------------------------------------------------------------------------[getPassword]
    public String getPassword() {
        return Password;
    }
}
