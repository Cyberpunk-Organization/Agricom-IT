package com.example.loginapp.model;

public class RegisterRequest {


    private String username;


    private String email;

    private String password;

    private String role;

    private String name;

    private String surname;


    public RegisterRequest(String name, String surname, String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
//        this.role = role;
        this.name = name;
        this.surname = surname;
    }

    // getters (optional setters if you need them)
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
