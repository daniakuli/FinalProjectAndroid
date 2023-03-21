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

    public void insertUser(User user){
        new Thread(() -> {
            appDatabase.usersDao().insertUser(user);
        }).start();
    }

    public void getUserByEmail(String userEmail){
      //return appDatabase.usersDao().getUserByEmail(userEmail);
    }

    public void updatePicture(Pictures picture){
        new Thread(() ->{
            appDatabase.picturesDao().update(picture);
        }).start();
    }

    public LiveData<List<Pictures>> getAllPictures() {
        return appDatabase.picturesDao().getAllPictures();
    }

    public LiveData<List<Pictures>> getThisUserPictures(String email){
        return appDatabase.picturesDao().getThisUserPictures(email);
    }

    public LiveData<List<Pictures>> getAllOtherPictures(String email){
        return appDatabase.picturesDao().getAllOtherPictures(email);
    }
}
