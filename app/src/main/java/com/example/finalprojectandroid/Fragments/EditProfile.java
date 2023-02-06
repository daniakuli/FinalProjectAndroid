package com.example.finalprojectandroid.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Register;
import com.example.finalprojectandroid.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class EditProfile extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    private SharedPreferences sharedPreferences;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final String USERNAME = "username";
    String uid = "";
    String pass = "";
    String imgPath = "";
    Uri filePath;
    ImageView imgEdit;
    Boolean picChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("users");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");

        imgEdit = view.findViewById(R.id.editPic);
        EditText emailET  = view.findViewById(R.id.edit_email);
        EditText usernameET = view.findViewById(R.id.edit_username);
        Button   backBtn  = view.findViewById(R.id.edit_backBtn);
        Button   saveBtn  = view.findViewById(R.id.edit_save);
        FloatingActionButton addPic = view.findViewById(R.id.editProfilePicbtn);


        sharedPreferences = getActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(user.getUsername().equals(sharedPreferences.getString(USERNAME, ""))){
                        emailET.setEnabled(false);
                        emailET.setText(user.getEmail());
                        usernameET.setText(user.getUsername());
                        Picasso.get().load(user.getImage()).into(imgEdit);
                        uid = snapshot.getKey();
                        pass = user.getPassword();
                        imgPath = user.getImage();
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(picChanged){
                    imgPath = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                    Toast.makeText(getActivity(),
                            "Profile picure Changed",
                                  Toast.LENGTH_LONG).show();
                }
                User editUser = new User(usernameET.getText().toString(),
                                        emailET.getText().toString(),
                                        pass,
                                        imgPath);
                databaseReference.child(uid).setValue(editUser);
                sharedPreferences.edit().putString(USERNAME,usernameET.
                                                              getText().
                                                              toString()).commit();
                getParentFragmentManager().popBackStack();
            }
        });

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePic();
            }
        });

        return view;
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
                        filePath = intent.getData();
                        try{
                            Bitmap bitmap = MediaStore.
                                    Images.
                                    Media.
                                    getBitmap(getActivity().getContentResolver(),
                                            filePath);
                            imgEdit.setImageBitmap(bitmap);
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
                        "images/" + imgName);

        ref.putFile(filePath)
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
}