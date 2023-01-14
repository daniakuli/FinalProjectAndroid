package com.example.finalprojectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class Register extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText emailET       = findViewById(R.id.email);
        final EditText usernameET    = findViewById(R.id.username);
        final EditText passwordET    = findViewById(R.id.password);
        final EditText conPassET     = findViewById(R.id.confirm_password);
        final Button registerBtn     = findViewById(R.id.registerButton);
        final TextView loginNowBtn   = findViewById(R.id.login_now);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email    = emailET.getText().toString();
                final String username = usernameET.getText().toString();
                final String password = passwordET.getText().toString();
                final String confPass = conPassET.getText().toString();

                if(!password.equals(confPass)){
                    Toast.makeText(Register.this, "Password are not matching", Toast.LENGTH_LONG).show();
                }
                else{
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(username))
                                Toast.makeText(Register.this, "E-mail already exists", Toast.LENGTH_LONG).show();
                            else{
                                databaseReference.child("users").child(username).child("email").setValue(email);
                                databaseReference.child("users").child(username).child("password").setValue(password);
                                Toast.makeText(Register.this, "User Registered", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}