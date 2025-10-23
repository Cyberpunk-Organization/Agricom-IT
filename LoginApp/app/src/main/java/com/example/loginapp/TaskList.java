package com.example.loginapp;

import java.util.List;
//import com.package.utils.TaskClass; // Wrong package see line below for fix
import com.example.loginapp.model.Task;
import com.example.loginapp.model.User;
//import com.package.utils.User; // Trying to import other class but not sure if it works


public class TaskList extends Task {  // need the user class to work
    private int TaskListID;
    private User Worker = new User();
    private List<Task> Tasks; //

    public TaskList(){};

    public TaskList(int TaskListID, User Worker, List<Task> Tasks){
        this.TaskListID = TaskListID;
        this.Worker = Worker;
        this.Tasks = Tasks;
    }

    public User GetUser( int UserID){


        // need to get information of the user

        return null; // TODO: Temp return plug. Replace when function is written
    }

    public void addTask(Task newTask){
        Tasks.add( new Task() );

    }
    public void editTask(){
        //need vaildation on this function, its from draw.io class diagram
    }
    public List<Task> printTask(){return Tasks;}



}
