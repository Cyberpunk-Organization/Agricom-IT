package com.example.agricom_it;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.LoginResponse;
import com.example.agricom_it.ui.ToDoListFragment;

public class TaskActivity extends AppCompatActivity {

    private static final String TAG = "TaskActivity";
    private final AuthApiService apiService = ApiClient.getService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);// Layout containing a <FrameLayout> as container

        int userID = -1;
        Intent intent = getIntent();

        Log.d(TAG, "onCreate: TaskActivity started");

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
                Log.d(TAG, "Retrieved userID: " + userID);
            } else {
                // fallback if loginResponse isn’t properly passed
                Log.w(TAG, "LoginResponse null — using fallback userID: " + userID);
            }
        } else {
            Log.w(TAG, "Intent missing 'login_id', defaulting to test user 3");
        }

        // Attach ToDoListFragment with userID
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

        Log.d(TAG, "Task fragment initialized for userID = " + userID);
    }
}
