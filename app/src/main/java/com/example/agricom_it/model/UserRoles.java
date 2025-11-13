package com.example.agricom_it.model;

public class UserRoles {
    private int RolesID;
    private int UserID;
    private String RolesType;

    //-----------------------------------------------------------------------------------[UserRoles]
    public UserRoles() {}

    //-----------------------------------------------------------------------------------[UserRoles]
    public UserRoles(int RolesID, int UserID, String RolesType) {
        this.RolesID = RolesID;
        this.UserID = UserID;
        this.RolesType = RolesType;
    }

    //-----------------------------------------------------------------------------------[getRoleID]
    public int getRoleID() { return RolesID; }

    //---------------------------------------------------------------------------------[getRoleType]
    public String getRoleType() { return RolesType; }

    //---------------------------------------------------------------------------------[setRoleType]
    public void setRoleType(String RoleType) { RolesType = RolesType; }
}