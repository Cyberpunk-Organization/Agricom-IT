package com.example.agricom_it.api;

import com.example.agricom_it.R;
import com.example.agricom_it.model.LoginRequest;
import com.example.agricom_it.model.RegisterRequest;
import com.example.agricom_it.model.LoginResponse;
import com.example.agricom_it.model.RegisterResponse;


import java.util.Date;
import java.util.Map;


import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface AuthApiService {

    //POST for login.php (unchanged)
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    // POST to register.php using form-encoded fields (no RegisterRequest POJO needed)
//    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register( @Body RegisterRequest request);

    @GET("inventory.php")
    Call<ResponseBody> getItem(@Query("action") String cmd, @Query("itemID") int itemID);
    @GET("inventory.php")
    Call<ResponseBody> inventory(@Query("action") String action, @QueryMap Map<String, Integer> params);
    @GET("inventory.php")
    Call<ResponseBody> getInventoryItems(@Query("action") String action, @Query("userID") int userID);
    @GET("inventory.php")
    Call<ResponseBody> AddItem(@Query("action") String action, @Query("name") String name);
    @GET("inventory.php")
    Call<ResponseBody> addItemToInventory(@Query("action") String action, @Query("inventoryID") int inventoryID, @Query("itemID") int itemID, @Query("quantity") int quantity);

    @GET("inventory.php")
    Call<ResponseBody> getInventoryID(@Query("action") String action, @Query("userID") int userID);

    @GET("inventory.php")
    Call<ResponseBody> updateItemQuantity(@Query("action") String action, @Query("inventoryID") int inventoryID, @Query("itemID") int itemID, @Query("quantity") int quantity);

    @GET("inventory.php")
    Call<ResponseBody> removeItemFromInventory(@Query("action") String action, @Query("inventoryID") int inventoryID, @Query("itemID") int itemID);

    @GET("tasks.php")
    Call<ResponseBody> AddTask(@Query("action") String action, @Query("dueDate") String DueDate, @Query("isDone") boolean isDone, @Query("task") String taskDesc );

    @GET("tasks.php")
    Call<ResponseBody> GetTaskListID(@Query("action") String action, @Query("workerID") int workerID);

    @GET("tasks.php")
    Call<ResponseBody> AddTaskList(@Query("action") String action, @Query("workerID") int workerID);

    @GET("tasks.php")
    Call<ResponseBody> AddTaskToTasklist(@Query("action") String action, @Query("taskListID") int taskListID, @Query("taskID") int taskID);

    @GET("tasks.php")
    Call<ResponseBody> GetTasksFromTasklist(@Query("action") String action, @Query("taskListID") int taskListID);
}
