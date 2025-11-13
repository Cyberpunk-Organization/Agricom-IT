package com.example.agricom_it.model;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role;
    private String name;
    private String surname;

    //-----------------------------------------------------------------------------[RegisterRequest]
    public RegisterRequest(String name, String surname, String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
//        this.role = "user";
        this.name = name;
        this.surname = surname;
    }

    //---------------------------------------------------------------------------------[getUsername]
    public String getUsername() {return username;}

    //-------------------------------------------------------------------------------------[getEmail]
    public String getEmail() {return email;}

    //----------------------------------------------------------------------------------[getPassword]
    public String getPassword() {return password;}

    //-------------------------------------------------------------------------------------[getRole]
    public String getRole() {return role;}
}
