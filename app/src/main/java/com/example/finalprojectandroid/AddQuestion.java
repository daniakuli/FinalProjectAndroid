package com.example.finalprojectandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalprojectandroid.Activites.Register;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;


import java.io.IOException;
import java.util.UUID;

public class AddQuestion extends AppCompatActivity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");
    Boolean picChanged = false;
    Uri fileUri;
    ImageView pProfileImage;
    private static final String USERNAME = "username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question);
        pProfileImage         = findViewById(R.id.profilePicReg);
        EditText country                = findViewById(R.id.country);
        EditText city                   = findViewById(R.id.city);
        FloatingActionButton addPic   = findViewById(R.id.addProfilePic);
        Button uploadButton             = findViewById(R.id.insertButton);

        SharedPreferences sharedPreferences = getSharedPreferences("app_pref", Context.MODE_PRIVATE);
        String currUsername = sharedPreferences.getString(USERNAME,"");

        addPic.setOnClickListener(view -> {
            choosePic();
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fileUri == null){
                    fileUri = Uri.parse("android.resource://com.example.finalprojectandroid/" + R.drawable.user1);
                }
//                if(!password.equals(confPass)){
//                    Toast.makeText(Register.this, "Password are not matching", Toast.LENGTH_LONG).show();
//                }
                else{
                    String imgPath = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                    Log.d("Check", imgPath);
                    String picID = databaseReference.child("places").push().getKey();
                    Pictures question = new Pictures(currUsername, imgPath, country.getText().toString(), city.getText().toString());
                    if (picID != null) {
                        databaseReference.child("places").child(picID).setValue(question)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(AddQuestion.this,
                                            "Question registered successfully",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(AddQuestion.this,
                                        "Question registered failed",
                                        Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }
    private void choosePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mStartForResult.launch(intent);
    }
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null) {
                        picChanged = true;
                        Intent intent = result.getData();
                        fileUri = intent.getData();
                        try{
                            Bitmap bitmap = MediaStore.
                                    Images.
                                    Media.
                                    getBitmap(getContentResolver(),
                                            fileUri);
                            pProfileImage.setImageBitmap(bitmap);
                        }
                        catch (IOException err){
                            err.printStackTrace();
                        }
                    }
                }
            });
    private String uploadImage(){
        String imgName = UUID.randomUUID().toString();
        StorageReference ref =
                storageReference.child(
                        "places/" + imgName);

        ref.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("ok", "Working");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("not ok","Not working");
                    }
                });
        return imgName;
    }
    private String uploadQuestion(){
        return "aaa";
    }

    }
