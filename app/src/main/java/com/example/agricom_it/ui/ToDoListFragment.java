package com.example.agricom_it.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.R;
import com.example.agricom_it.TaskAdapter;
import com.example.agricom_it.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToDoListFragment extends Fragment {

    private EditText editTextTask;
    private Button buttonAdd;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private int userID = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todolist, container, false);

        if (getArguments() != null && getArguments().containsKey("userID")) {
            userID = getArguments().getInt("userID", -1);
        }

        editTextTask = view.findViewById(R.id.editTextTask);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        recyclerView = view.findViewById(R.id.rvTasks);

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            String desc = editTextTask.getText().toString().trim();
            if (!desc.isEmpty()) {
                Task task = new Task(taskList.size() + 1, desc, false, new Date());
                taskList.add(task);
                adapter.notifyItemInserted(taskList.size() - 1);
                editTextTask.setText("");
                Toast.makeText(getContext(), "Task added locally", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
