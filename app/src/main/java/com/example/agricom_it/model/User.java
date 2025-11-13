package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("Email")
    private String Email;
    @SerializedName("Name")
    private String Name;
    @SerializedName("Surname")
    private String Surname;
    @SerializedName("Username")
    private String Username;
    @SerializedName("Role")
    private String Role;


    //--------------------------------------------------------------------[User]
//    public User() {};

    //--------------------------------------------------------------------[User]
//    public User(int UserID,String Email ,String Name,String Surname, String Username, String Role) {
//        this.id = UserID;
//        this.Email = Email;
//        this.Name = Name;
//        this.Surname = Surname;
//        this.Username = Username;
//        this.Role = Role;
//    }
    //---------------------------------------------------------------[getUserID]
    public int getUserID() { return id; }

    //----------------------------------------------------------------[getEmail]
    public String getEmail() { return this.Email; }

    //-----------------------------------------------------------------[getName]
    public String getName() { return this.Name; }

    //---------------------------------------------------------------[getSurname]
    public String getSurname() { return this.Surname; }

    //-------------------------------------------------------------[getUsername]
    public String getUsername() { return this.Username; }

    //-----------------------------------------------------------------[getRole]
    public String getRole() { return this.Role; }
}
