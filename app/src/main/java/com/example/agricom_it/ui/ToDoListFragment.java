package com.example.agricom_it.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agricom_it.R;
import com.example.agricom_it.TaskAdapter;
import com.example.agricom_it.api.ApiClient;
import com.example.agricom_it.api.AuthApiService;
import com.example.agricom_it.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ToDoListFragment extends Fragment {

    private EditText editTextTask;
    private Button buttonAdd;
    private Button btnPickDate;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private int userID = -1;
    private static final String TAG = "TaskActivity";
    private final AuthApiService apiService = ApiClient.getService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todolist, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey("userID"))
        {
            userID = args.getInt("userID", -1);
        }

        if (getArguments() != null && getArguments().containsKey("userID")) {
            userID = getArguments().getInt("userID", -1);
        }

        editTextTask = view.findViewById(R.id.editTextTask);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        btnPickDate = view.findViewById(R.id.btnPickDate);
        recyclerView = view.findViewById(R.id.rvTasks);

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnPickDate.setOnClickListener(v -> {
            // Get current date
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Show DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (DatePicker datePickerView, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        btnPickDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        buttonAdd.setOnClickListener(v -> {
            String desc = editTextTask.getText().toString().trim();
            Date date = btnPickDate.getText().toString().trim().isEmpty() ? null : new Date();

            if (!desc.isEmpty()) {
                //Task task = new Task(taskList.size() + 1, desc, false, date);
                //taskList.add(task);
                AddTaskToServer(desc, date);
                adapter.notifyItemInserted(taskList.size() - 1);
                editTextTask.setText("");
                btnPickDate.setText("");
                Toast.makeText(getContext(), "Task added locally", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), "Task added locally", Toast.LENGTH_SHORT).show(); //TODO
            }
        });
        if (userID >= 0)
        {
            Log.d(TAG, "Loading inventory for userID: " + userID);
            //loadInventoryForUser(userID);
        }
        return view;
    }

    private void AddTaskToServer(String task, Date dueDate) {
        boolean isDone = false;
        Call<ResponseBody> call = apiService.Addtask("AddTask", dueDate, isDone, task);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "✅ Task sent to server", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Task added successfully");
                } else {
                    Toast.makeText(getContext(), "⚠ Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Server error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "❌ Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error", t);
            }
        });
    }
}
