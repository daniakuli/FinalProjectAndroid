package com.example.finalprojectandroid.Models;

public class User {
    private String username;
    private String email;
    private String image;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }


    public String getImage() {return image;}

    public Integer getScore() {
        return score;
    }
}