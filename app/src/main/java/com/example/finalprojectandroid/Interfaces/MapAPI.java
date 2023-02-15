package com.example.finalprojectandroid.Interfaces;


import com.example.finalprojectandroid.Models.LocationResponse;
import com.example.finalprojectandroid.Models.Pictures;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MapAPI {
        @GET("geocode/json")
        Call<LocationResponse> geocode(@Query("address") String address,
                                       @Query("key") String key);
}
