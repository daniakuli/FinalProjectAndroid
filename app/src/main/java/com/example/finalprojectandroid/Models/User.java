
package com.example.finalprojectandroid.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    private String email = "";
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "imgUrl")
    private String image;
    @ColumnInfo(name = "score")
    private Integer score;

    public User() {
    }

    public User(String username,
                String email,
                String image,
                Integer score) {
        this.username = username;
        this.email = email;
        this.image = image;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() { return email; }

    public String getImage() {return image;}

    public Integer getScore() { return score; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) { this.email = email; }

    public void setImage(String image) { this.image = image; }

    public void setScore(Integer score) { this.score = score; }
}