package com.example.agricom_it.model.map;

public class Coordinates {
    private double latitude;
    private double longitude;

    //-----------------------------------------------------------------------------------[coordinates]
    public Coordinates() {

    }

    //-----------------------------------------------------------------------------------[coordinates]
    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //--------------------------------------------------------------------------------[getLatitude]
    public double getLatitude() {
        return latitude;
    }

    //--------------------------------------------------------------------------------[setLatitude]
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    //-------------------------------------------------------------------------------[getLongitude]
    public double getLongitude() {
        return longitude;
    }

    //-------------------------------------------------------------------------------[setLongitude]
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    //--------------------------------------------------------------------------------[toJsonString]
    public String toJsonString() {
        return "{" +
                "\"latitude\": " + latitude + "," +
                "\"longitude\": " + longitude +
                "}";
    }

    /*
    Example JSON format for Coordinates
    {
        'latitude': 10,
        'longitude': 10
    }
     */
}
