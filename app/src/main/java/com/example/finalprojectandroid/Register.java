package com.example.finalprojectandroid;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectandroid.DataBase.Pair;
import com.example.finalprojectandroid.DataBase.UploadImages;
import com.example.finalprojectandroid.Fragments.ImageDialogFragment;
import com.example.finalprojectandroid.Fragments.RegDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Register extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 22;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");
    private StorageReference storageReference;
    private Uri filePath;
    private ImageView picIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

                       picIV         = findViewById(R.id.profilePicReg);
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
        picIV.setImageResource(images[numb]);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*UploadImages upImg = new UploadImages(Register.this);
                Pair p = upImg.choosePic();
                Log.d("checkPair","Bool: " +
                                            p.picChanged +
                                            " filePath: " +
                                            p.filepath);*/
                choosePic();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email    = emailET.getText().toString();
                final String username = usernameET.getText().toString();
                final String password = passwordET.getText().toString();
                final String confPass = conPassET.getText().toString();
                if(filePath == null){
                    filePath = Uri.parse("android.resource://com.example.finalprojectandroid/" + images[numb]);
                }
                if(!password.equals(confPass)){
                    Toast.makeText(Register.this, "Password are not matching", Toast.LENGTH_LONG).show();
                }
                else{
                    String imgPath = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                    String userID = databaseReference.child("users").push().getKey();
                    User user = new User(username, email, password, imgPath);
                    databaseReference.child("users").child(userID).setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Register.this,
                                            "User registered successfully",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this,
                                            "User registered successfully",
                                            Toast.LENGTH_SHORT).show();
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

    private void choosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mStartForResult.launch(intent);
    }

    private String uploadImage(){
        String imgName = UUID.randomUUID().toString();
        StorageReference ref =
                storageReference.child(
                        "images/" + imgName);

        Log.d("check",filePath.toString());
        ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Register.this,
                                "Image uploaded",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast
                                .makeText(Register.this,
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        return imgName;
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null) {
                        Intent intent = result.getData();
                        filePath = intent.getData();
                        try{
                            Bitmap bitmap = MediaStore.
                                    Images.
                                    Media.
                                    getBitmap(getContentResolver(),
                                            filePath);
                            picIV.setImageBitmap(bitmap);
                        }
                        catch (IOException err){
                            err.printStackTrace();
                        }
                    }
                }
            });
}