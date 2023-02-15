package com.example.finalprojectandroid.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String LOGGED_IN = "logged_in";
    private static final String USERNAME = "username";
    private static final String SCORE = "0";
    public Integer score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("users");

        final EditText usernameET  = findViewById(R.id.username);
        final EditText passwordET  = findViewById(R.id.password);
        final Button   loginBtn    = findViewById(R.id.loginButton);
        final TextView regNowBtn   = findViewById(R.id.register_now);

        sharedPreferences = getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        loginBtn.setOnClickListener(view -> {

            final String username = usernameET.getText().toString().trim();
            final String password = passwordET.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(Login.this, "Enter Username!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean userExists = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);

                        if (user != null && user.getUsername().equals(username) && user.getPassword().equals(password)) {
                            userExists = true;
                            if(user.getScore() == null)
                                score = 0;
                            else
                                score = user.getScore();
                            break;
                        }
                    }
                    if (userExists) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(LOGGED_IN, true);
                        editor.putString(USERNAME, username);
                        editor.putString(SCORE, score.toString());
                        editor.apply();
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Login.this, "Login failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        regNowBtn.setOnClickListener(view -> startActivity(new Intent(Login.this, Register.class)));
    }
}