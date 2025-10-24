package com.example.agricom_it.api;

import com.example.agricom_it.model.LoginRequest;
import com.example.agricom_it.model.RegisterRequest;
import com.example.agricom_it.model.LoginResponse;
import com.example.agricom_it.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    //POST for login.php (unchanged)
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    // POST to register.php using form-encoded fields (no RegisterRequest POJO needed)
//    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register( @Body RegisterRequest request);
}
