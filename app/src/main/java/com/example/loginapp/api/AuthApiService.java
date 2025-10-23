package com.example.loginapp.api;

import com.example.loginapp.model.RegisterRequest;
import com.example.loginapp.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApiService {

    //POST for login.php (unchanged)
    @POST("login.php")
    Call<LoginResponse> login(@Body RegisterRequest request);

    // POST to register.php using form-encoded fields (no RegisterRequest POJO needed)
    @FormUrlEncoded
    @POST("register.php")
    Call<LoginResponse> register(@Body RegisterRequest request);;
}
