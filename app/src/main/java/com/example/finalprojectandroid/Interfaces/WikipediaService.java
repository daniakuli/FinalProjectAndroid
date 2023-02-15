package com.example.finalprojectandroid.Interfaces;

import com.example.finalprojectandroid.Models.WikipediaResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikipediaService {
    @GET("api.php")
    Call<WikipediaResponse> getArticle(@Query("action") String action,
                                       @Query("format") String format,
                                       @Query("prop") String prop,
                                       @Query("titles") String title);
}