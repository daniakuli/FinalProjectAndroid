package com.example.finalprojectandroid.Activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Toast;

import com.example.finalprojectandroid.AddQuestion;
import com.example.finalprojectandroid.Fragments.HomePage;
import com.example.finalprojectandroid.Fragments.ProfilePage;
import com.example.finalprojectandroid.Fragments.QnAPage;
import com.example.finalprojectandroid.Fragments.ViewQuestion;
import com.example.finalprojectandroid.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String LOGGED_IN = "logged_in";
    private static final String USERNAME = "username";


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
            String username = sharedPreferences.getString(USERNAME, "");
            setContentView(R.layout.activity_main);

            NavigationBarView bottomNavigationView = findViewById(R.id.bottom_navigation);

            bottomNavigationView.setSelectedItemId(R.id.home_item);

            loadFragment(new HomePage());

            bottomNavigationView.setOnItemSelectedListener(item -> {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.home_item:
                        fragment = new HomePage();
                        break;
                    case R.id.profile_item:
                        fragment = new ProfilePage();
                        break;
                    case R.id.add_question:
                        fragment = new AddQuestion();
                        break;
                    case R.id.view_question:
                        fragment = new ViewQuestion();
                }
                if(fragment != null)
                        loadFragment(fragment);
                return true;
            });

        }
    }
    void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,fragment)
                .commit();
    }
}