package com.example.loginapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapp.R;
import com.example.loginapp.TaskAdapter;
import com.example.loginapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ToDoListFragment extends Fragment {

    private EditText editTextTask;
    private Button buttonAdd;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todolist, container, false);

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
                Task task = new Task(taskList.size() + 1, desc, false, null);
                taskList.add(task);
                adapter.notifyItemInserted(taskList.size() - 1);
                editTextTask.setText("");
            }
        });

        return view;
    }
}
