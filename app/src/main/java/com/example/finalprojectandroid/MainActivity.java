package com.example.finalprojectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.Toast;

import com.example.finalprojectandroid.Fragments.HomePage;
import com.example.finalprojectandroid.Fragments.ProfilePage;
import com.example.finalprojectandroid.Fragments.QnAPage;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String LOGGED_IN = "logged_in";
    private static final String USERNAME = "username";
    private static final String SCORE = "0";
    private NavigationBarView bottomNavigationView;


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

            bottomNavigationView = findViewById(R.id.bottom_navigation);

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    switch (item.getItemId()){
                        case R.id.question_item:
                            fragment = new QnAPage();
                        case R.id.home_item:
                            fragment = new HomePage();
                            break;
                        case R.id.profile_item:
                            fragment = new ProfilePage();
                            break;
                    }
                    if(fragment != null)
                            loadFragment(fragment);
                    return true;
                }
                void loadFragment(Fragment fragment){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container,fragment)
                            .commit();
                }
            });

            /*if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new ProfilePage())
                        .commit();
            }*/
        }
    }
}