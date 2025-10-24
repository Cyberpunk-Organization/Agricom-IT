package com.example.agricom_it.api;

import com.example.agricom_it.InventoryActivity;
import com.example.agricom_it.model.InventoryItem;
import com.example.agricom_it.model.LoginRequest;
import com.example.agricom_it.model.RegisterRequest;
import com.example.agricom_it.model.LoginResponse;
import com.example.agricom_it.model.RegisterResponse;

import java.util.Date;
import java.util.List;
import com.example.agricom_it.model.Task;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface AuthApiService {

    //POST for login.php (unchanged)
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    // POST to register.php using form-encoded fields (no RegisterRequest POJO needed)
//    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register( @Body RegisterRequest request);


    @GET("inventory")
    Call<String> AddItem( @Body String name, @Body Double cost );
    Call<List<InventoryItem>> UpdateItemName( @Body int ID, @Body String name );
    Call<String> GetItem( @Body int ID );
    Call<String> GetItemID( @Body String name );
    Call<String> AddItemToInventory( @Body int InventoryID, @Body int ItemID, @Body int Quantity );
    Call<String> UpdateItemQuantity( @Body int InventoryID, @Body int ItemID, @Body int Quantity );
    Call<String> RemoveItemFromInventory( @Body int InventoryID, @Body int ItemID );
//    Call<String> GetItemsFromInventory( @Body int InventoryID );
//    Call<String> GetItemQuantity( @Body int InventoryID, @Body int ItemID );
    Call<String> AddInventory( @Body int FarmID, @Body int ReportID );
    Call<String> UpdateInventory( @Body int FarmID, @Body int InventoryID );

    @GET("farms")
    Call<String> AddFarm(@Body int FarmerID );
    Call<Integer> GetFarmID(@Body int FarmerID );

    @GET("tasks")
    Call<String> AddTask(@Body Date Duedate, @Body boolean isDone, @Body Task task );
    Call<String> UpdateTask(@Body Date Duedate, @Body boolean isDone, @Body Task task, @Body int TaskID );
    Call<String> RemoveTask(@Body int TaskID );
    Call<String> GetTask(@Body int TaskID );
    Call<String> AddTaskToTasklist(@Body int TaskListID, @Body int TaskID );
    Call<String> RemoveTaskFromTasklist( @Body int TaskID );
    Call<String> GetUserTasks(@Body int UserID );
    Call<String> GetTaskFromTasklist(@Body int TaskListID );
    Call<String> AddTasklist(@Body String name );
    Call<String> UpdateTasklist( @Body int TaskListID, @Body int WorkerID );
    Call<String> RemoveTasklist(@Body int TaskListID );
    Call<String> GetTasklistID(@Body int WorkerID );
    Call<String> GetTasklistContent(@Body int TaskListID );




}
