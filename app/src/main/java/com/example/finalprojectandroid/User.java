package com.example.finalprojectandroid;

public class User {
    private String username;
    private String email;
    private String password;
    private String image;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String password, String image) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {return image;}

    public void setImage(String image) {
        this.image = image;
    }

}