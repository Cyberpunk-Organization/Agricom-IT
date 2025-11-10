package com.example.agricom_it.model;

//Class that assigns the user to a role

public class UserRoles {
    private int RolesID;
    private int UserID;
    private String RolesType;
    public UserRoles(){};

    public UserRoles(int RolesID,int UserID, String RolesType){
        this.RolesID = RolesID;
        this.UserID = UserID;
        this.RolesType = RolesType;
    };

    public int GetRolesID(){return RolesID;}
    public String GetRolesType(){return RolesType;}
    public void SetRoleType(String RoleType){RolesType = RolesType;}
}