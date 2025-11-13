package com.example.agricom_it.model;

public class MapComment {
    private int userID;
    private int mapID;
    private double latitude;
    private double longitude;
    private String comment;

    //----------------------------------------------------------------------------------[MapComment]
    public MapComment(int userID, int mapID, double latitude, double longitude, String comment) {
        this.userID = userID;
        this.mapID = mapID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
    }
}
