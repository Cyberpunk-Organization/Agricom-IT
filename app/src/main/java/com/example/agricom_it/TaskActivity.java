package com.example.agricom_it;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.LoginResponse;
import com.example.agricom_it.ui.ToDoListFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskActivity extends AppCompatActivity {

    private static final String TAG = "TaskActivity";
    private final AuthApiService apiService = ApiClient.getService();
    private int userID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task); // use a container layout, not fragment layout

        Log.d(TAG, "onCreate: TaskActivity started");

        // Retrieve userID (similar to InventoryActivity)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("login_id")) {
            Object extra = intent.getSerializableExtra("login_id");
            LoginResponse loginResponse = null;

            if (extra instanceof LoginResponse) {
                loginResponse = (LoginResponse) extra;
            } else {
                try {
                    loginResponse = intent.getParcelableExtra("login_id");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to retrieve LoginResponse from intent", e);
                }
            }

            if (loginResponse != null) {
                userID = loginResponse.getID();
                Log.d(TAG, "Retrieved userID: " + userID);
            } else {
                // fallback (temporary)
                userID = intent.getIntExtra("login_id", 3);
                Log.w(TAG, "LoginResponse null — using fallback userID: " + userID);
            }
        }

        // Load ToDoListFragment into activity
        ToDoListFragment toDoListFragment = new ToDoListFragment();
        Bundle args = new Bundle();
        args.putInt("userID", userID);
        toDoListFragment.setArguments(args);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.taskContainer, toDoListFragment)
                    .commit();
        }

        // Example backend call (test only)
        Date dueDate = new Date();
        addTask(dueDate, false, "Test backend task");
    }

    /**
     * Calls the backend API to add a new task.
     */
    private void addTask(Date dueDate, boolean isDone, String taskDescription) {
        String dueDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dueDate);

        // Use the proper API service call
        Call<String> call = apiService.addTask(taskDescription, isDone, dueDate);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Task added successfully: " + response.body());
                } else {
                    Log.e(TAG, "Failed to add task: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Error adding task: " + t.getMessage());
            }
        });
    }
}
