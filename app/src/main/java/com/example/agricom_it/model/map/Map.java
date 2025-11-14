package com.example.agricom_it.model.map;

import java.util.List;

public class Map
{
    private int mapID;
    private int userID;
    private List<MapArea> areas;
    private List<MapComment> comments;


    //-----------------------------------------------------------------------------------------[map]
    public Map()
    {

    }

    //-----------------------------------------------------------------------------------------[map]
    public Map(int userID)
    {
        this.userID = userID;
    }

    //-----------------------------------------------------------------------------------------[map]
    public Map(int mapID, int userID, List<MapArea> areas, List<MapComment> comments)
    {
        this.mapID = mapID;
        this.userID = userID;
        this.areas = areas;
        this.comments = comments;
    }

    public Map(int userID, List<MapArea> areas, List<MapComment> comments)
    {
        this.userID = userID;
        this.areas = areas;
        this.comments = comments;
    }

    public String toJsonString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"mapID\": ").append(mapID).append(",");
        sb.append("\"userID\": ").append(userID).append(",");

        sb.append("\"areas\": [");
        for (int i = 0; i < areas.size(); i++) {
            sb.append(areas.get(i).toJsonString());
            if (i < areas.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("],");

        sb.append("\"comments\": [");
        for (int i = 0; i < comments.size(); i++) {
            sb.append(comments.get(i).toJsonString());
            if (i < comments.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    //-------------------------------------------------------------------------------------[addArea]
    public void addArea(MapArea area)
    {
        this.areas.add(area);
    }

    //----------------------------------------------------------------------------------[addComment]
    public void addComment(MapComment comment)
    {
        this.comments.add(comment);
    }

    //------------------------------------------------------------------------------------[getMapID]
    public int getMapID()
    {
        return mapID;
    }

    //------------------------------------------------------------------------------------[setMapID]
    public void setMapID(int mapID)
    {
        this.mapID = mapID;
    }

    //-----------------------------------------------------------------------------------[getUserID]
    public int getUserID()
    {
        return userID;
    }

    //-----------------------------------------------------------------------------------[setUserID]
    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    //------------------------------------------------------------------------------------[getAreas]
    public List<MapArea> getAreas()
    {
        return areas;
    }

    //------------------------------------------------------------------------------------[setAreas]
    public void setAreas(List<MapArea> areas)
    {
        this.areas = areas;
    }

    //---------------------------------------------------------------------------------[getComments]
    public List<MapComment> getComments()
    {
        return comments;
    }

    //---------------------------------------------------------------------------------[setComments]
    public void setComments(List<MapComment> comments)
    {
        this.comments = comments;
    }

/*
    Example JSON string for multiple areas:
    [
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
        },
        {
            'areaID': 2,
            'coordinates':
            [
                {'latitude': 30, 'longitude': 30},
                {'latitude': 40, 'longitude': 30},
                {'latitude': 40, 'longitude': 40}
            ]
        },
        {
            'areaID': 2,
            'coordinates':
            [
                {'latitude': 30, 'longitude': 30},
                {'latitude': 40, 'longitude': 41},
                {'latitude': 40, 'longitude': 24},
                {'latitude': 40, 'longitude': 64},
                {'latitude': 40, 'longitude': 7},
                {'latitude': 40, 'longitude': 0},
                {'latitude': 40, 'longitude': 796}
            ]
        },
    ];


    Example JSON string for multiple comments:
    [
        {
            'commentID': 1,
            'coordinates': {'latitude': 12, 'longitude': 15},
            'text': 'This is a comment'
        },
        {
            'commentID': 2,
            'coordinates': {'latitude': 22, 'longitude': 25},
            'text': 'This is another comment'
        },
        {
            'commentID': 3,
            'coordinates': {'latitude': 32, 'longitude': 35},
            'text': 'Yet another comment'
        }
    ]
*/
}
