package com.example.agricom_it.model.map;

import android.annotation.SuppressLint;

import java.util.List;
import com.example.agricom_it.model.map.Coordinates;

public class MapComment
{
    private String commentID;
    private int userID;
    private Coordinates coordinates;
    private String text;

    //----------------------------------------------------------------------------------[MapComment]
    public MapComment()
    {

    }

    //----------------------------------------------------------------------------------[MapComment]
    public MapComment( int userID, Coordinates coordinates, String comment )
    {
        this.userID = userID;
        this.coordinates = coordinates;
        this.text = comment;
        setCommentID( coordinates );
    }

    //----------------------------------------------------------------------------------[MapComment]
    public MapComment( String coordString, String comment )
    {
        // Parse the coordString to create the list of coordinates
        String[] pairs = coordString.split(";");
        for (String pair : pairs)
        {
            String[] latLon = pair.split(",");
            double latitude = Double.parseDouble(latLon[0]);
            double longitude = Double.parseDouble(latLon[1]);
            this.coordinates = (new Coordinates(latitude, longitude));
        }

        setCommentID( this.coordinates);

        this.text = comment;
    }

    //--------------------------------------------------------------------------------[getCommentID]
    public String getCommentID()
    {
        return commentID;
    }

    //--------------------------------------------------------------------------------[setCommentID]
    public void setCommentID(Coordinates coordinates )
    {

        this.commentID = String.format("%.3f", coordinates.getLatitude()).replace(",", "")
                .replace("-", "m") +
                String.format("%.3f", coordinates.getLongitude())
                        .replace(",", "")
                        .replace("-", "m");

    }

    //------------------------------------------------------------------------------[getCommentText]
    public String getCommentText()
    {
        return text;
    }

    //------------------------------------------------------------------------------[setCommentText]
    public void setCommentText(String comment)
    {
        this.text = comment;
    }

    //--------------------------------------------------------------------------------[toJsonString]
    public String toJsonString()
    {
        return "{" +
                "\"commentID\": " + commentID + "," +
                "\"userID\": " + userID + "," +
                "\"coordinates\": " + coordinates.toJsonString() + "," +
                "\"comment\": \"" + text + "\"" +
                "}";
    }
}
