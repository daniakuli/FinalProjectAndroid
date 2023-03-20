package com.example.finalprojectandroid.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private static final String LOGGED_IN = "logged_in";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String FULL_EMAIL = "fullEmail";
    private static final String SCORE = "0";
    public Integer score;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
                //.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("users");

        final EditText emailET  = findViewById(R.id.email);
        final EditText passwordET  = findViewById(R.id.password);
        final Button   loginBtn    = findViewById(R.id.loginButton);
        final TextView regNowBtn   = findViewById(R.id.register_now);

        sharedPreferences = getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        loginBtn.setOnClickListener(view -> {

            final String email = emailET.getText().toString().trim();
            final String password = passwordET.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Login.this, "Enter Username!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();

                            databaseReference.child("users").child(email.substring(0, email.indexOf('.'))).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        User user = task.getResult().getValue(User.class);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean(LOGGED_IN, true);
                                        assert user != null;
                                        editor.putString(USERNAME, user.getUsername());
                                        editor.putString(FULL_EMAIL, email);
                                        editor.putString(EMAIL, email.substring(0, email.indexOf('.')));
                                        editor.putString(SCORE, user.getScore().toString());
                                        editor.apply();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        });

        regNowBtn.setOnClickListener(view -> startActivity(new Intent(Login.this, Register.class)));
    }
}