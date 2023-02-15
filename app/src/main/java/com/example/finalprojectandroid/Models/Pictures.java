package com.example.finalprojectandroid.Models;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.finalprojectandroid.Interfaces.OnGetDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Pictures {
    private static final String USERNAME = "username";
    private String username;
    private String image;
    private String country;
    private String city;

    public Pictures() {
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

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public void getData(Context context, OnGetDataListener listener, Boolean isProfile) {
        List<Pictures> picturesList = new ArrayList<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        String name = sharedPreferences.getString(USERNAME,"");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("places");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Pictures pic = snapshot.getValue(Pictures.class);
                    if (pic != null) {
                        if(isProfile && pic.username.equals(name)) {
                            picturesList.add(pic);
                        }
                        else if(!isProfile && !pic.username.equals(name)){
                            picturesList.add(pic);
                        }
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
