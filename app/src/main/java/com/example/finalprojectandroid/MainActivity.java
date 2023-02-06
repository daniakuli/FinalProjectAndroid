package com.example.finalprojectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import com.example.finalprojectandroid.Fragments.ProfilePage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String LOGGED_IN = "logged_in";
    private static final String USERNAME = "username";
    private static final String SCORE = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("users");

        SharedPreferences sharedPreferences = getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        if(!sharedPreferences.getBoolean(LOGGED_IN, false))
        {
            finish();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        else
        {
            String username = sharedPreferences.getString(USERNAME, "");
            Toast.makeText(MainActivity.this, "User " + username +" Logged in", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new ProfilePage())
                        .commit();
            }
        }
    }
}