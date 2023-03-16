package com.example.finalprojectandroid.Activites;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class Register extends AppCompatActivity {

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");
    private StorageReference storageReference;
    private Uri fileUri;
    private ImageView picImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");

        addPic.setOnClickListener(view -> {
            /*UploadImages upImg = new UploadImages(Register.this);
            Pair p = upImg.choosePic();
            Log.d("checkPair","Bool: " +
                                        p.picChanged +
                                        " filePath: " +
                                        p.filepath);*/
            choosePic();
        });

        registerBtn.setOnClickListener(view -> {
            final String email    = emailET.getText().toString();
            final String username = usernameET.getText().toString();
            final String password = passwordET.getText().toString();
            final String confPass = conPassET.getText().toString();
            if(fileUri == null){
                fileUri = Uri.parse("android.resource://com.example.finalprojectandroid/" + images[numb]);
            }
            if(!password.equals(confPass)){
                Toast.makeText(Register.this, "Password are not matching", Toast.LENGTH_LONG).show();
            }
            else{
                String imgPath = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                String userID = databaseReference.child("users").push().getKey();
                User user = new User(username, email, password, imgPath, 0);
                if (userID != null) {
                    databaseReference.child("users").child(userID).setValue(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(Register.this,
                                        "User registered successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(Register.this,
                                    "User registered failed",
                                    Toast.LENGTH_SHORT).show());
                }
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

    private String uploadImage(){
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
    }

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
}