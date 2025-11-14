package com.example.agricom_it.model.map;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapArea {
    private String areaID;
    private List<Coordinates> coordinates = new ArrayList<>();

    private final String TAG = "MapArea";

    //-------------------------------------------------------------------------------------[MapArea]
    public MapArea() {}

    //-------------------------------------------------------------------------------------[MapArea]
    public MapArea(String areaID, List<Coordinates> coordinates) {
        this.areaID = areaID;
        this.coordinates = (coordinates != null) ? coordinates : new ArrayList<>();
    }

    //-------------------------------------------------------------------------------------[MapArea]
    public MapArea(List<Coordinates> coordinates) {
        this.coordinates = (coordinates != null) ? coordinates : new ArrayList<>();
    }

    //-------------------------------------------------------------------------------------[MapArea]
    @SuppressLint("DefaultLocale")
    public MapArea(String coordString) {
        this.coordinates = new ArrayList<>();

        if (coordString == null || coordString.trim().isEmpty()) {
            setAreaID();
            return;
        }

        String[] pairs = coordString.split(";");
        for (String pair : pairs) {
            if (pair == null) continue;
            pair = pair.trim();
            if (pair.isEmpty()) continue;

            String[] latLon = pair.split(",");
            if (latLon.length < 2) continue;

            try {
                double latitude = Double.parseDouble(latLon[0].trim());
                double longitude = Double.parseDouble(latLon[1].trim());

                Log.d(TAG, "Parsed coordinate: " + latitude + ", " + longitude);

                Coordinates c = new Coordinates(latitude, longitude);
                this.coordinates.add(c);
            } catch (NumberFormatException e) {
                Log.d(TAG, "Skipping invalid coordinate: " + pair);
            }
        }

        setAreaID();
    }

    //------------------------------------------------------------------------------[getCoordinates]
    public List<Coordinates> getCoordinates() { return coordinates; }

    //-------------------------------------------------------------------------------[setCoordinates]
    public void setCoordinates(List<Coordinates> coordinates) { this.coordinates = coordinates; }

    //-----------------------------------------------------------------------------------[getAreaID]
    public String getAreaID() { return areaID; }

    //-----------------------------------------------------------------------------------[setAreaID]
    @SuppressLint("DefaultLocale")
    public void setAreaID() {
        StringBuilder idString = new StringBuilder();
        for (Coordinates coord : this.coordinates) {
            if(coord == null)
            {
                continue;
            }

            idString.append(String.format("%.3f", coord.getLatitude()).replace(",", "")
                            .replace("-", "m"))
                    .append(String.format("%.3f", coord.getLongitude())
                            .replace(",", "")
                            .replace("-", "m"));

        }

        this.areaID = idString.toString();
    }

    //--------------------------------------------------------------------------------[toJsonString]
    public String toJsonString() throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("areaID", (areaID != null) ? areaID : JSONObject.NULL);

        JSONArray arr = new JSONArray();
        if (coordinates != null) {
            for (Coordinates c : coordinates) {
                if (c == null) continue;
                JSONObject coord = new JSONObject();
                coord.put("latitude", c.getLatitude());
                coord.put("longitude", c.getLongitude());
                arr.put(coord);
            }
        }

        obj.put("coordinates", arr);
        return obj.toString();
    }


/*"
Example JSON format for MapArea
{
    'areaID': 1,
    'coordinates':
    [
        {'latitude': 10, 'longitude': 10},
        {'latitude': 20, 'longitude': 10},
        {'latitude': 20, 'longitude': 20},
        {'latitude': 10, 'longitude': 20},
        {'latitude': 15, 'longitude': 19}
    ]
}
    */
}