package com.example.finalprojectandroid.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.finalprojectandroid.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String LOGGED_IN = "logged_in";
    private static final String USERNAME = "username";
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        if(!sharedPreferences.getBoolean(LOGGED_IN, false))
        {
            finish();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        }
        else
        {
            setContentView(R.layout.activity_main);

            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
            navController = navHostFragment.getNavController();

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.home_item);

            NavigationUI.setupWithNavController(bottomNavigationView, navController);


            bottomNavigationView.setOnItemSelectedListener(item -> {
                switch (item.getItemId()){
                    case R.id.home_item:
                        navController.navigate(R.id.homePage);
                        break;
                    case R.id.profile_item:
                        navController.navigate(R.id.profilePage);
                        break;
                    case R.id.add_question_item:
                        navController.navigate(R.id.addQuestionPage);
                        break;
                    case R.id.view_question_item:
                        navController.navigate(R.id.viewQuestionPage);
                        break;
                }
                return true;
            });

        }
    }
}