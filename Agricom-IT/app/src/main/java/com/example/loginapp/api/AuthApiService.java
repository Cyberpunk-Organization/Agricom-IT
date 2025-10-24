package com.example.agricom_it.api;

import com.example.agricom_it.model.LoginRequest;
import com.example.agricom_it.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    //POST for login.php
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    //POST to register.php
    @POST("register.php")
    Call<LoginResponse> register(@Body LoginRequest request);
}
