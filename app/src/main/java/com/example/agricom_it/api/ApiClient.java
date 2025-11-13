package com.example.agricom_it.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://afrimart.virtuocloud.co.za/api/";
    // keep retrofit private if you don't want it referenced elsewhere
    public static Retrofit retrofit = null;

    //----------------------------------------------------------------------------------[getService]
    // make this public static so other classes can call ApiClient.getService()
    public static AuthApiService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AuthApiService.class);
    }
}
