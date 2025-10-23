package com.example.loginapp.model;

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


    //-------------------------------------------------------------[Constructor]
    public User() {};
    public User(int UserID,String Email ,String Name,String Surname, String Username) {
        this.UserID = UserID;
        this.Email = Email;
        this.Name = Name;
        this.Surname = Surname;
        this.Username = Username;
    }
    //---------------------------------------------------------------[getUserID]
    public int getUserID(){return this.UserID;}
    public String getEmail(){return this.Email;}
    public String getName(){return this.Name;}

    public String getSurname(){return this.Surname;}
    public String getUsername(){return this.Username;}

}
