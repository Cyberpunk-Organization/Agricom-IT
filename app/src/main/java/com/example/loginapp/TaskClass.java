package com.example.loginapp;

import java.util.Date;

public class TaskClass {
    private int TaskID;
    private String Description;
    private boolean Is_Done;
    private Date DueDate;

    public TaskClass(int TaskID, String Description, boolean Is_Done, Date DueDate) {
        this.TaskID = TaskID;
        this.Description = Description;
        this.Is_Done = Is_Done;
        this.DueDate = DueDate;
    }
    public int GetTaskID() {return TaskID;}
    public String GetDescription() {return Description;}
    public boolean getIS_Done() {return Is_Done;}
    public Date getDueDate() {return DueDate;}
    public void setDescription(String description) {Description = description;}
    public boolean complete(){return Is_Done = true;}

}
