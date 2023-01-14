package com.example.finalprojectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameET  = findViewById(R.id.username);
        final EditText passwordET  = findViewById(R.id.password);
        final Button   loginBtn    = findViewById(R.id.loginButton);
        final TextView regNowBtn   = findViewById(R.id.register_now);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = usernameET.getText().toString();
                final String password = passwordET.getText().toString();

            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(username)){
                        final String getPassword = snapshot.child(username).child("password").getValue(String.class);

                        if(getPassword.equals(password)){
                            Toast.makeText(Login.this, "Login successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }
                        else{
                            Toast.makeText(Login.this, "Check Username or Password", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            }
        });

        regNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }
}