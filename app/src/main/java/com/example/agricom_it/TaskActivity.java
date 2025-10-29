package com.example.agricom_it;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskActivity extends AppCompatActivity {
    private static final String TAG = "TaskActivity";
    private final AuthApiService apiService = ApiClient.getService();
    private int userID = 3;//for user=testE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_todolist);
        Intent intent = getIntent();
        userID = intent.getIntExtra("login_id", 1);
        Log.d(TAG, "FINALLY: " + userID);
        Date dueDate = new Date();
        addTask(dueDate, false, "Test task"); //test values
    }

    private void addTask(Date dueDate, boolean isDone, String task) {
        String dueDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dueDate);
        Call<String> call = apiService.addTask(task, isDone,dueDate);
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