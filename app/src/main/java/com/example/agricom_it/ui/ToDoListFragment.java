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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
    private int currentTaskListId = -1;
    private int userID = -1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_todolist, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey("userID"))
        {
            userID = args.getInt("userID", -1);
        }

        editTextTask = view.findViewById(R.id.editTextTask);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        btnPickDate = view.findViewById(R.id.btnPickDate);
        recyclerView = view.findViewById(R.id.rvTasks);

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);



        btnPickDate.setOnClickListener(v -> showDatePicker());
        Log.d(TAG, "onCreateView");

        ensureTasklistExistsAndLoad();

        buttonAdd.setOnClickListener(v -> {
            String description = editTextTask.getText().toString().trim();

            if (description.isEmpty() || selectedDate.isEmpty())
            {
                Toast.makeText(getContext(), "Please enter task and select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentTaskListId <= 0)
            {
                // Ensure tasklist then add
                ensureTasklistExists(() -> addTaskToServer(description, selectedDate));
            }
            else
            {
                addTaskToServer(description, selectedDate);
            }
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

    private void ensureTasklistExistsAndLoad()
    {
        ensureTasklistExists(this::loadTasksForCurrentTasklist);
    }

    private void ensureTasklistExists(Runnable onDone) {
        Log.d(TAG, "ensureTasklistExists called");

        int workerId = userID;
        if (workerId <= 0)
        {
            Log.e(TAG, "Invalid worker id");
            return;
        }

        Log.d(TAG, "WorkerID: " + workerId);

        Call<ResponseBody> call = apiService.GetTaskListID("GetTaskListID", userID );
        Log.d(TAG, "GetTaskListID call created");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
            {
                Log.d(TAG, "GetTaskListID response received");
                if (!response.isSuccessful())
                {
                    Log.e(TAG, "GetTaskListID server error: " + response.code());
                    return;
                }
                try
                {
                    String json = response.body().string();
                    JsonObject root = new GsonBuilder().create().fromJson(json, JsonObject.class);
                    JsonElement data = root.get("data");
                    if (data != null && !data.isJsonNull())
                    {
                        // tasks.php returns plain column value - may be string/number
                        int taskListId = data.getAsInt();
                        currentTaskListId = taskListId;
                        if (onDone != null) onDone.run();
                        return;
                    }
                    // not found -> create
                    Call<ResponseBody> addCall = apiService.AddTaskList("AddTaskList", workerId);
                    addCall.enqueue(new Callback<ResponseBody>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response)
                        {
                            if (!response.isSuccessful())
                            {
                                Log.e(TAG, "AddTaskList server error: " + response.code());
                                return;
                            }
                            try
                            {
                                String json = response.body().string();
                                JsonObject root = new GsonBuilder().create().fromJson(json, JsonObject.class);
                                JsonElement data = root.get("data");
                                if (data != null && data.isJsonObject())
                                {
                                    JsonObject obj = data.getAsJsonObject();
                                    if (obj.has("TaskListID"))
                                    {
                                        currentTaskListId = obj.get("TaskListID").getAsInt();
                                        if (onDone != null) onDone.run();
                                    }
                                }
                            } catch (IOException e)
                            {
                                Log.e(TAG, "AddTaskList parse error", e);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            Log.e(TAG, "AddTaskList network error", t);
                        }
                    });

                } catch (IOException e) {
                    Log.e(TAG, "GetTaskListID parse error", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "GetTaskListID network error", t);
            }
        });
    }

    private void loadTasksForCurrentTasklist() {
        if (currentTaskListId <= 0) return;
        Call<ResponseBody> call = apiService.GetTasksFromTasklist("GetTasksFromTasklist", currentTaskListId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "GetTasksFromTasklist server error: " + response.code());
                    return;
                }
                try {
                    String json = response.body().string();
                    JsonObject root = new GsonBuilder().create().fromJson(json, JsonObject.class);
                    JsonElement data = root.get("data");
                    taskList.clear();
                    if (data != null && data.isJsonArray()) {
                        JsonArray arr = data.getAsJsonArray();
                        for (JsonElement el : arr) {
                            JsonObject o = el.getAsJsonObject();
                            Task t = new Task();
                            if (o.has("Task")) t.setDescription(o.get("Task").getAsString());
                            if (o.has("Is_Done")) t.setDone(o.get("Is_Done").getAsInt() != 0);
                            if (o.has("DueDate")) {
                                try {
                                    Date d = serverDateFormat.parse(o.get("DueDate").getAsString());
                                    t.setDueDate(d);
                                } catch (Exception ignored) {}
                            }
                            if (o.has("TaskID")) t.setId(o.get("TaskID").getAsInt());
                            taskList.add(t);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (IOException e) {
                    Log.e(TAG, "GetTasksFromTasklist parse error", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "GetTasksFromTasklist network error", t);
            }
        });
    }

    private void addTaskToServer(String taskDesc, String DueDate) {
        boolean isDone = false;
        Log.d(TAG, "Adding task: " + taskDesc + " | Due: " + DueDate + " | isDone: " + isDone );

        // AddTask first
        Call<ResponseBody> call = apiService.AddTask("AddTask", DueDate, isDone ? true : false, taskDesc);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful())
                {
                    Log.d(TAG, "Processing AddTask response");
                    try
                    {
                        String json = response.body().string();
                        JsonObject root = new GsonBuilder().create().fromJson( json, JsonObject.class );
                        JsonElement data = root.get("data");
                        int createdTaskId;
                        if (data != null && data.isJsonObject()) {
                            JsonObject obj = data.getAsJsonObject();
                            if (obj.has("TaskID")) createdTaskId = obj.get("TaskID").getAsInt();
                            else
                            {
                                createdTaskId = -1;
                            }
                        }
                        else
                        {
                            createdTaskId = -1;
                        }

                        // ensure tasklist and then link
                        if (currentTaskListId <= 0) {
                            ensureTasklistExists(() -> linkTaskToTasklist(createdTaskId, taskDesc, DueDate));
                        } else {
                            linkTaskToTasklist(createdTaskId, taskDesc, DueDate);
                        }
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

    private void linkTaskToTasklist(int createdTaskId, String desc, String dueDate) {
        if (createdTaskId <= 0 || currentTaskListId <= 0) {
            Log.e(TAG, "Invalid ids for linking task. taskListId=" + currentTaskListId + " taskId=" + createdTaskId);
            return;
        }
        Call<ResponseBody> call = apiService.AddTaskToTasklist("AddTaskToTasklist", currentTaskListId, createdTaskId);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "AddTaskToTasklist server error: " + response.code());
                    return;
                }
                Log.d(TAG, "Task linked to tasklist successfully.");
                // optionally refresh the list
                loadTasksForCurrentTasklist();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "AddTaskToTasklist network error", t);
            }
        });
    }


}
