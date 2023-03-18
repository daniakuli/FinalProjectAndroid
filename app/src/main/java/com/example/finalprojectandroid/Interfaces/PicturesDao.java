package com.example.finalprojectandroid.Interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finalprojectandroid.Models.Pictures;

import java.util.List;

@Dao
public interface PicturesDao {

    @Query("SELECT * FROM pictures")
    LiveData<List<Pictures>> getAllPictures();

    @Insert
    void insertPicture(Pictures picture);

    @Delete
    void deletePicture(Pictures picture);

    @Query("DELETE FROM pictures")
    void deleteAllPictures();
}
