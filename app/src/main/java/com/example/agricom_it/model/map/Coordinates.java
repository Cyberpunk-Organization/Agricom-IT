package com.example.agricom_it.model.map;

import org.json.JSONException;
import org.json.JSONObject;

public class Coordinates {
    private double latitude;
    private double longitude;

    //-----------------------------------------------------------------------------------[coordinates]
    public Coordinates() {}

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
        try {
            JSONObject obj = new JSONObject();
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            return obj.toString();
        } catch (JSONException e) {
            return "{}";
        }
    }

}
