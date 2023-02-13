package com.example.finalprojectandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pictures {
    private static final String USERNAME = "username";
    private FirebaseDatabase database;
    private String username;
    private String image;
    private String country;
    private String city;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase firebaseDatabase;

    public Pictures() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Pictures(String username,String image, String country, String city) {
        this.username = username;
        this.image = image;
        this.country = country;
        this.city = city;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public void getData(Context context, OnGetDataListener listener, Boolean isProfile) {
        List<Pictures> picturesList = new ArrayList<>();

        sharedPreferences = context.getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        String name = sharedPreferences.getString(USERNAME,"");

        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("places");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Pictures pic = snapshot.getValue(Pictures.class);
                    if(isProfile && pic.username.equals(name)) {
                        Log.d("name", pic.username);
                        picturesList.add(pic);
                    }
                    else if(!isProfile && !pic.username.equals(name)){
                        Log.d("name", "true?" +
                                !pic.username.equals(name));
                        picturesList.add(pic);
                    }
                }
                listener.onSuccess(picturesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
