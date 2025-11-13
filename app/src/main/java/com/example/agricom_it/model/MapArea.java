package com.example.agricom_it.model;

public class MapArea {
    private int userID;
    private int mapID;
    private String coordinates; // Saved in the form of "lat1,lon1;lat2,lon2;lat3,lon3" ~ i don't know if this is how you want it evert?

    //-------------------------------------------------------------------------------------[MapArea]
    public MapArea(int userID, int mapID, String coordinates) {
        this.userID = userID;
        this.mapID = mapID;
        this.coordinates = coordinates;
    }
}
