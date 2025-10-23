package com.example.loginapp.api;

import com.example.loginapp.model.LoginRequest;
import com.example.loginapp.model.RegisterRequest;
import com.example.loginapp.model.LoginResponse;
import com.example.loginapp.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AuthApiService {

    //POST for login.php (unchanged)
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    // POST to register.php using form-encoded fields (no RegisterRequest POJO needed)
//    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register( @Body RegisterRequest request);
}
