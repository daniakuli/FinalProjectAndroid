package com.example.finalprojectandroid.Models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.finalprojectandroid.Interfaces.PicturesDao;
import com.example.finalprojectandroid.Interfaces.UsersDao;

@Database(entities = {Pictures.class, User.class}, version = 15)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract PicturesDao picturesDao();

    public abstract UsersDao usersDao();

    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                                            AppDatabase.class,
                                            "app_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
        }
        return instance;
    }
}
