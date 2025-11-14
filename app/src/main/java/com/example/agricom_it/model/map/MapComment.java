package com.example.agricom_it.model.map;

import android.annotation.SuppressLint;

import java.util.List;

import com.example.agricom_it.model.map.Coordinates;

import org.json.JSONException;
import org.json.JSONObject;

public class MapComment {
    private String commentID;
    private int userID;
    private Coordinates coordinates;
    private String text;

    //----------------------------------------------------------------------------------[MapComment]
    public MapComment() {}

    //----------------------------------------------------------------------------------[MapComment]
    public MapComment(int userID, Coordinates coordinates, String comment) {
        this.userID = userID;
        this.coordinates = coordinates;
        this.text = comment;
        setCommentID(coordinates);
    }

    //----------------------------------------------------------------------------------[MapComment]
    public MapComment(String coordString, String comment) {
        // Parse the coordString to create the list of coordinates
        String[] pairs = coordString.split(";");
        for (String pair : pairs) {
            String[] latLon = pair.split(",");
            double latitude = Double.parseDouble(latLon[0]);
            double longitude = Double.parseDouble(latLon[1]);
            this.coordinates = (new Coordinates(latitude, longitude));
        }

        setCommentID(this.coordinates);

        this.text = comment;
    }

    //--------------------------------------------------------------------------------[getCommentID]
    public String getCommentID() {
        return commentID;
    }

    //--------------------------------------------------------------------------------[setCommentID]
    public void setCommentID(Coordinates coordinates) {

        this.commentID = String.format("%.3f", coordinates.getLatitude()).replace(",", "")
                .replace("-", "m") +
                String.format("%.3f", coordinates.getLongitude())
                        .replace(",", "")
                        .replace("-", "m");

    }

    //------------------------------------------------------------------------------[getCommentText]
    public String getCommentText() {
        return text;
    }

    //------------------------------------------------------------------------------[setCommentText]
    public void setCommentText(String comment) {
        this.text = comment;
    }

    //--------------------------------------------------------------------------------[toJsonString]
    public String toJsonString() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("commentID", (commentID != null) ? commentID : JSONObject.NULL);
            obj.put("userID", userID);

            if (coordinates != null) {
                // Coordinates.toJsonString returns a JSON string, convert to JSONObject
                try {
                    obj.put("coordinates", new JSONObject(coordinates.toJsonString()));
                } catch (JSONException e) {
                    obj.put("coordinates", JSONObject.NULL);
                }
            } else {
                obj.put("coordinates", JSONObject.NULL);
            }

            obj.put("comment", (text != null) ? text : JSONObject.NULL);

            return obj.toString();
        } catch (JSONException e) {
            return "{}";
        }
    }
}
