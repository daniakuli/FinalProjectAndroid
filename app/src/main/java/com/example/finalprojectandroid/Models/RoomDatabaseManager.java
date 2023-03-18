package com.example.finalprojectandroid.Models;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RoomDatabaseManager {
    private final AppDatabase appDatabase;

    public RoomDatabaseManager(Context context){
        appDatabase = AppDatabase.getInstance(context);
    }

    public void insertPicture(Pictures picture){
        new Thread(() -> {
            appDatabase.picturesDao().insertPicture(picture);
        }).start();
    }

    public LiveData<List<Pictures>> getAllPictures() {
        return appDatabase.picturesDao().getAllPictures();
    }
}
