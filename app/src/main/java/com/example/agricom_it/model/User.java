package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    //TODO: integrate with api? Else add sql controls to retrieve data
    //TODO: Get the user roles
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


    //TODO uncomment if needed.
    //-------------------------------------------------------------[Constructor]
//    public User() {};
//    public User(int UserID,String Email ,String Name,String Surname, String Username, String Role) {
//        this.id = UserID;
//        this.Email = Email;
//        this.Name = Name;
//        this.Surname = Surname;
//        this.Username = Username;
//        this.Role = Role;
//    }
    //---------------------------------------------------------------[getUserID]
    public int getUserID(){return id;}
    public String getEmail(){return this.Email;}
    public String getName(){return this.Name;}
    public String getSurname(){return this.Surname;}
    public String getUsername(){return this.Username;}
    public String getRole(){return this.Role;}

    //TODO: id getter from login intent. See DashboardActivity.java line 23 - 35

}
