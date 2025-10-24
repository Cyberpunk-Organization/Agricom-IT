package com.example.agricom_it.model;

//class for the purpose of the farm zone that need to worked on

public class ZoneRole {
    private int PurposeID;
    private String Purpose;

    public ZoneRole(){};

    public ZoneRole(int PurposeID,String Purpose){
      this.PurposeID = PurposeID;
      this.Purpose = Purpose;
    };

    public int GetPurposeID(){return PurposeID;}

    public String GetPurpose(){return Purpose;}

    public void setPurpose(String Purpose){this.Purpose = Purpose;}

}
