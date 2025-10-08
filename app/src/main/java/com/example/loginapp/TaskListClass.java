package com.example.loginapp;

import java.util.List;
import java.io.*;
//import com.package.utils.TaskClass; // Wrong package see line below for fix
import com.example.loginapp.TaskClass;

//import com.package.utils.User; // Trying to import other class but not sure if it works

//TODO: I'll create user class, then I'll import it - Evert


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

        return null; //TODO: Temp return plug. Replace when function is written
    }

    public void addTask(Task newTask){tasks.add(new Task);}
    public void editTask(){
        //need vaildation on this function, its from draw.io class diagram
    }
    public List<Task> printTask(){return Tasks;}



}
