package com.example.loginapp.model;

public class User {

    //TODO: integrate with api? Else add sql controls to retrieve data
    //TODO: Get the user roles
    private int UserID;
    private String Email;
    private String Name;
    private String Password;
    private String Surname;
    private String Username;


    //-------------------------------------------------------------[Constructor]
    public User() {};
    public User(int UserID,String Email ,String Name, String Password,String Surname, String Username) {
        this.UserID = UserID;
        this.Email = Email;
        this.Name = Name;
        this.Password = Password;
        this.Surname = Surname;
        this.Username = Username;
    }
    //---------------------------------------------------------------[getUserID]
    public int getUserID(){return UserID;}
    public String getEmail(){return Email;}
    public String getName(){return Name;}
    public String getPassword(){return Password;}
    public String getSurname(){return Surname;}
    public String getUsername(){return Username;}

}
