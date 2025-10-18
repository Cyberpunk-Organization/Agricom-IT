package com.example.loginapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.*;
import java.util.ArrayList;

import com.example.loginapp.R;
import com.example.loginapp.ui.ToDoList;

public class ToDoList extends AppCompatActivity {
    EditText editTextTask;
    Button buttonAdd;
    ListView listViewTasks;
    ArrayList<String> taskList;
    ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        editTextTask = findViewById(R.id.editTextTask); TODO see todo layout
        buttonAdd = findViewById(R.id.buttonAdd);
        listViewTasks = findViewById(R.id.listViewTasks);

        taskList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskList);
        listViewTasks.setAdapter(adapter);

        // Add new task
        buttonAdd.setOnClickListener(v -> {
            String task = editTextTask.getText().toString().trim();
            if (!task.isEmpty()) {
                taskList.add(task);
                adapter.notifyDataSetChanged();
                editTextTask.setText("");
            } else {
                Toast.makeText(ToDoList.this, "Please enter a task", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove task on long press
        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            taskList.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(ToDoList.this, "Task deleted", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

}
