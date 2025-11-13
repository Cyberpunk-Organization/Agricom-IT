package com.example.agricom_it.model;

public class ZoneRole {
    private int PurposeID;
    private String Purpose;

    //------------------------------------------------------------------------------------[ZoneRole]
    public ZoneRole() {}

    //------------------------------------------------------------------------------------[ZoneRole]
    public ZoneRole(int PurposeID, String Purpose) {
        this.PurposeID = PurposeID;
        this.Purpose = Purpose;
    }

    //--------------------------------------------------------------------------------[getPurposeID]
    public int getPurposeID() { return PurposeID; }

    //----------------------------------------------------------------------------------[getPurpose]
    public String getPurpose() { return Purpose; }

    //----------------------------------------------------------------------------------[setPurpose]
    public void setPurpose(String Purpose) { this.Purpose = Purpose; }

}
