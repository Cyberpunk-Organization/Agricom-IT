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


    public Task() {}

    public Task(int taskID, String description, boolean isDone, Date dueDate) {
        this.taskID = taskID;
        this.description = description;
        this.isDone = isDone;
        this.dueDate = dueDate;
    }

    public int getTaskID() { return taskID; }
    public String getDescription() { return description; }
    public boolean isDone() { return isDone; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setDescription(String description) { this.description = description; }
    public void setDone(boolean done) { isDone = done; }

    public boolean complete() {
        return isDone = true;
    }

    public void setId(int taskID) { this.taskID = taskID; }
    public int getId() { return taskID; }
}
