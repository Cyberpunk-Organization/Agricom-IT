package com.example.loginapp;

import java.util.List;
import java.io.*;
import com.package.utils.TaskClass;
import com.package.utils.User;  // Trying to import other class but not sure if it works

public class TaskListClass extends User,TaskClass {  // need the user class to work
    private int TaskListID;
    private User Worker;
    private List<Task> Tasks;

    public TaskListClass(int TaskListID,User Worker, List<Task> Tasks){
        this.TaskListID = TaskListID;
        this.Worker = Worker;
        this.Tasks = Tasks;
    }

    public User GetUser(){
        // need to get information of the user
    }
    public addTask(Task newTask){tasks.add(new Task);}
    public editTask(){
        //need vaildation on this function, its from draw.io class diagram
    }
    public printTask(){return Tasks;}



}
