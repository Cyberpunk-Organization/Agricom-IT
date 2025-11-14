package com.example.agricom_it.model.map;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Map {
    private int mapID;
    private int userID;
    private List<MapArea> areas;
    private List<MapComment> comments;
    private final String TAG = "Map";


    //-----------------------------------------------------------------------------------------[map]
    public Map() {}

    //-----------------------------------------------------------------------------------------[map]
    public Map(int userID) {
        this.userID = userID;
    }

    //-----------------------------------------------------------------------------------------[map]
    public Map(int mapID, int userID, List<MapArea> areas, List<MapComment> comments) {
        this.mapID = mapID;
        this.userID = userID;
        this.areas = areas;
        this.comments = comments;
    }

    //-----------------------------------------------------------------------------------------[map]
    public Map(int userID, List<MapArea> areas, List<MapComment> comments) {
        this.userID = userID;
        this.areas = areas;
        this.comments = comments;
    }

    //---------------------------------------------------------------------------------[toJsonString]
    public String toJsonString() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("mapID", mapID);
            obj.put("userID", userID);

            JSONArray areasArr = new JSONArray();
            if (areas != null) {
                for (MapArea area : areas) {
                    if (area == null) continue;
                    try {
                        // MapArea.toJsonString may throw JSONException; wrap per-item
                        areasArr.put(new JSONObject(area.toJsonString()));
                    } catch (JSONException e) {
                        Log.e(TAG, "Invalid area JSON: " + e.getMessage());
                    }
                }
            }
            obj.put("areas", areasArr);

            JSONArray commentsArr = new JSONArray();
            if (comments != null) {
                for (MapComment comment : comments) {
                    if (comment == null) continue;
                    try {
                        commentsArr.put(new JSONObject(comment.toJsonString()));
                    } catch (JSONException e) {
                        // skip invalid comment
                    }
                }
            }
            obj.put("comments", commentsArr);

            return obj.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error creating map JSON: " + e.getMessage());
            return "{}";
        }
    }

    //-------------------------------------------------------------------------------------[addArea]
    public void addArea(MapArea area) {
        this.areas.add(area);
    }

    //----------------------------------------------------------------------------------[addComment]
    public void addComment(MapComment comment) {
        this.comments.add(comment);
    }

    //------------------------------------------------------------------------------------[getMapID]
    public int getMapID() {
        return mapID;
    }

    //------------------------------------------------------------------------------------[setMapID]
    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    //-----------------------------------------------------------------------------------[getUserID]
    public int getUserID() {
        return userID;
    }

    //-----------------------------------------------------------------------------------[setUserID]
    public void setUserID(int userID) {
        this.userID = userID;
    }

    //------------------------------------------------------------------------------------[getAreas]
    public List<MapArea> getAreas() {
        return areas;
    }

    //------------------------------------------------------------------------------------[setAreas]
    public void setAreas(List<MapArea> areas) {
        this.areas = areas;
    }

    //---------------------------------------------------------------------------------[getComments]
    public List<MapComment> getComments() {
        return comments;
    }

    //---------------------------------------------------------------------------------[setComments]
    public void setComments(List<MapComment> comments) {
        this.comments = comments;
    }
}
