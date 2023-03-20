package com.example.finalprojectandroid.Interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.finalprojectandroid.Models.User;

import java.util.List;

@Dao
public interface UsersDao {

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Insert
    void insertUser(User user);

    @Delete
    void deleteUser(User User);

    @Query("DELETE FROM users")
    void deleteAllPictures();

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
}
