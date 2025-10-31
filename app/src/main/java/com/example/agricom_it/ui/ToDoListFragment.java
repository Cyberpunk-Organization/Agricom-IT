package com.example.agricom_it.ui;

import android.app.DatePickerDialog;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToDoListFragment extends Fragment {

    private EditText editTextTask;
    private Button buttonAdd;
    private Button btnPickDate;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private String selectedDate = "";
    private static final String TAG = "ToDoListFragment";

    private final AuthApiService apiService = ApiClient.getService();
    private final SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todolist, container, false);

        editTextTask = view.findViewById(R.id.editTextTask);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        btnPickDate = view.findViewById(R.id.btnPickDate);
        recyclerView = view.findViewById(R.id.rvTasks);

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnPickDate.setOnClickListener(v -> showDatePicker());

        buttonAdd.setOnClickListener(v -> {
            String description = editTextTask.getText().toString().trim();

            if (description.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(getContext(), "Please enter task and select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            addTaskToServer(description, selectedDate);
        });

        return view;
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                getContext(),
                (DatePicker view, int y, int m, int d) -> {
                    calendar.set(y, m, d);
                    selectedDate = serverDateFormat.format(calendar.getTime());
                    btnPickDate.setText(selectedDate);
                },
                year, month, day
        );

        dialog.show();
    }

    private void addTaskToServer(String taskDesc, String DueDate) {
        boolean isDone = false;
        Log.d(TAG, "Adding task: " + taskDesc + " | Due: " + DueDate + " | isDone: " + isDone );

        Call<ResponseBody> call = apiService.AddTask("AddTask", DueDate, isDone, taskDesc);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful())
                {
                    try
                    {
                        String json = response.body().string();
                        JsonObject root = new GsonBuilder().create().fromJson( json, JsonObject.class );
                        Log.d(TAG, "Server response: " + root.toString() );
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }


                    Log.d(TAG, "✅ Server response OK");
                    Toast.makeText(getContext(), "Task added to server!", Toast.LENGTH_SHORT).show();

                    // Add to local list to show in RecyclerView
                    Task newTask = new Task();
                    newTask.setDescription(taskDesc);
                    newTask.setDone(false);

                    try
                    {
                        newTask.setDueDate(serverDateFormat.parse(DueDate));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    taskList.add(newTask);
                    adapter.notifyItemInserted(taskList.size() - 1);

                    // Reset fields
                    editTextTask.setText("");
                    btnPickDate.setText("Select Date");
                    selectedDate = "";
                } else {
                    Log.e(TAG, "⚠ Server error: " + response.code());
                    Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ Network error", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
