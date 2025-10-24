package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

public class User {

    //TODO: integrate with api? Else add sql controls to retrieve data
    //TODO: Get the user roles
    @SerializedName("userID")
    private int UserID;
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


    //-------------------------------------------------------------[Constructor]
    public User() {};
    public User(int UserID,String Email ,String Name,String Surname, String Username, String Role) {
        this.UserID = UserID;
        this.Email = Email;
        this.Name = Name;
        this.Surname = Surname;
        this.Username = Username;
        this.Role = Role;
    }
    //---------------------------------------------------------------[getUserID]
    public int getUserID(){return this.UserID;}
    public String getEmail(){return this.Email;}
    public String getName(){return this.Name;}
    public String getSurname(){return this.Surname;}
    public String getUsername(){return this.Username;}
    public String getRole(){return this.Role;}

}
