package com.example.finalprojectandroid.Activites;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectandroid.Models.FirebaseStorageManager;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class Register extends AppCompatActivity {

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
            //getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    
    private Uri fileUri;
    private ImageView picImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);

                       picImageView  = findViewById(R.id.profilePicReg);
        final EditText emailET       = findViewById(R.id.email);
        final EditText usernameET    = findViewById(R.id.username);
        final EditText passwordET    = findViewById(R.id.password);
        final EditText conPassET     = findViewById(R.id.confirm_password);
        final Button   registerBtn   = findViewById(R.id.registerButton);
        final TextView loginNowBtn   = findViewById(R.id.login_now);
        final FloatingActionButton
                       addPic        = findViewById(R.id.addProfilePic);

        int[] images = {R.drawable.user1,
                R.drawable.user2,
                R.drawable.user3,
                R.drawable.user4,
                R.drawable.user5};
        Random rand = new Random();
        int numb = rand.nextInt(images.length);
        picImageView.setImageResource(images[numb]);


        //FirebaseStorage storage = FirebaseStorage.getInstance();
        //storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");
        storageReference = new FirebaseStorageManager();

        addPic.setOnClickListener(view -> {
            choosePic();
        });

        registerBtn.setOnClickListener(view -> {
            final String email    = emailET.getText().toString();
            final String username = usernameET.getText().toString();
            final String password = passwordET.getText().toString();
            final String confPass = conPassET.getText().toString();

            String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
            String checkUserName = "^(?=.*[a-zA-Z0-9]).{2,}$";
            String checkPassword = "^(?=.*[a-zA-Z0-9])" +      //any letter or number
                    "(?=\\S+$)" +       //no white spaces
                    ".{6,}" +         //at least 6 characters
                    "$";

            if(fileUri == null){
                fileUri = Uri.parse("android.resource://com.example.finalprojectandroid/" + images[numb]);
            }
            if(!password.equals(confPass)){
                Toast.makeText(Register.this, "Password are not matching", Toast.LENGTH_LONG).show();
            }
            else if(!email.matches(checkEmail)) {
                Toast.makeText(Register.this,"Invalid Email!", Toast.LENGTH_SHORT).show();
            }
            else if(!username.matches(checkUserName)) {
                Toast.makeText(Register.this,"Username should contain at least 2 characters!", Toast.LENGTH_SHORT).show();
            }
            else if (!password.matches(checkPassword)) {
                Toast.makeText(Register.this,"Password should contain 6 characters!", Toast.LENGTH_SHORT).show();
            }
            else{
                String imgPath = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                registerToFireBaseAuth(email, password, username, imgPath);
            }
        });

        loginNowBtn.setOnClickListener(view -> finish());
    }

    private void choosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mStartForResult.launch(intent);
    }

    /*private String uploadImage(){
        String imgUID = UUID.randomUUID().toString();
        StorageReference ref =
                storageReference.child(
                        "images/" + imgUID);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(Register.this,
                        "Image uploaded",
                        Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast
                        .makeText(Register.this,
                                "Failed " + e.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show());
        return imgUID;
    }*/

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null) {
                        Intent intent = result.getData();
                        fileUri = intent.getData();
                        try{
                            Bitmap bitmap = MediaStore.
                                    Images.
                                    Media.
                                    getBitmap(getContentResolver(),
                                            fileUri);
                            picImageView.setImageBitmap(bitmap);
                        }
                        catch (IOException err){
                            err.printStackTrace();
                        }
                    }
                }
            });

    private void registerToFireBaseAuth(String email, String password, String username, String imgPath) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(username, email, imgPath, 0);
                        databaseReference.child("users").child(email.substring(0, email.indexOf('.'))).setValue(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

}