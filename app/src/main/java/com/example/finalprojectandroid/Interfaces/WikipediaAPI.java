package com.example.finalprojectandroid.Interfaces;

import com.example.finalprojectandroid.Models.CityWiki;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WikipediaAPI {
    @GET("/api/rest_v1/page/summary/{city}")
    Call<CityWiki> getWikiArticle(@Path("city") String city);

}
