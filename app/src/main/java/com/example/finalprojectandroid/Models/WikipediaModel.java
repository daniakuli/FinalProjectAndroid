package com.example.finalprojectandroid.Models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.finalprojectandroid.Interfaces.WikipediaAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WikipediaModel {
    final public static WikipediaModel instance = new WikipediaModel();

    final String BASE_URL = "https://en.wikipedia.org";
    Retrofit retrofit;
    WikipediaAPI wikiAPI;

    private WikipediaModel() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        wikiAPI = retrofit.create(WikipediaAPI.class);
    }

    public LiveData<CityWiki> getWikiData(String title){
        MutableLiveData<CityWiki> data = new MutableLiveData<>();
        Call<CityWiki> call = wikiAPI.getWikiArticle(title);
        call.enqueue(new Callback<CityWiki>() {

            @Override
            public void onResponse(Call<CityWiki> call, Response<CityWiki> response) {
                if(response.isSuccessful()) {
                    CityWiki res = response.body();
                    data.setValue(res);
                } else {
                    Log.d("TAG", "----------WikiSearchResult response error");
                }
            }

            @Override
            public void onFailure(Call<CityWiki> call, Throwable t) {
                Log.d("TAG", "----------WikiSearchResult fail");

            }
        });
        return data;
    }
}
