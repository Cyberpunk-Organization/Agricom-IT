package com.example.agricom_it.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Task {

    @SerializedName("TaskID")
    private int taskID;
    @SerializedName("Description")
    private String description;
    @SerializedName("Is_Done")
    private boolean isDone;
    @SerializedName("DueDate")
    private Date dueDate;

    //----------------------------------------------------------------------------------------[Task]
    public Task() {}

    //----------------------------------------------------------------------------------------[Task]
    public Task(int taskID, String description, boolean isDone, Date dueDate) {
        this.taskID = taskID;
        this.description = description;
        this.isDone = isDone;
        this.dueDate = dueDate;
    }

    //---------------------------------------------------------------------------------------[getId]
    public int getId() { return taskID; }

    //---------------------------------------------------------------------------------------[setId]
    public void setId(int taskID) { this.taskID = taskID; }

    //------------------------------------------------------------------------------[getDescription]
    public String getDescription() { return description; }

    //------------------------------------------------------------------------------[setDescription]
    public void setDescription(String description) { this.description = description; }

    //--------------------------------------------------------------------------------------[isDone]
    public boolean isDone() { return isDone; }

    //-------------------------------------------------------------------------------------[setDone]
    public void setDone(boolean done) { isDone = done; }

    //------------------------------------------------------------------------------------[complete]
    public boolean complete() {
        return isDone = true;
    }

    //----------------------------------------------------------------------------------[getDueDate]
    public Date getDueDate() { return dueDate; }

    //----------------------------------------------------------------------------------[setDueDate]
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
