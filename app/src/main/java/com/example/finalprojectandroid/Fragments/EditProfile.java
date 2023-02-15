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
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class EditProfile extends Fragment {
    private SharedPreferences sharedPreferences;
    private StorageReference storageReference;
    private static final String USERNAME = "username";
    private static final String SCORE = "0";
    private String uid = "";
    private String pass = "";
    private String imgPath = "";
    private String nameForUpdate;
    private String score;
    private Uri fileUri;
    private ImageView imageView;
    private Boolean picChanged = false;
    private ProfilePage profilePage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");//.child("users");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");

        imageView = view.findViewById(R.id.editPic);
        EditText emailET  = view.findViewById(R.id.edit_email);
        EditText usernameET = view.findViewById(R.id.edit_username);
        Button   backBtn  = view.findViewById(R.id.edit_backBtn);
        Button   saveBtn  = view.findViewById(R.id.edit_save);
        FloatingActionButton addPicBtn = view.findViewById(R.id.editProfilePicbtn);


        sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);
        nameForUpdate = sharedPreferences.getString(USERNAME, "");
        score = sharedPreferences.getString(SCORE,"");

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getUsername().equals(sharedPreferences.getString(USERNAME, ""))) {
                        emailET.setEnabled(false);
                        emailET.setText(user.getEmail());
                        usernameET.setText(user.getUsername());
                        Picasso.get().load(user.getImage()).into(imageView);
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

        backBtn.setOnClickListener(item -> getParentFragmentManager().popBackStack());

        saveBtn.setOnClickListener(item -> {
            if(picChanged){
                imgPath = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                Toast.makeText(getActivity(),
                        "Profile picture Changed",
                              Toast.LENGTH_LONG).show();
            }
            User editUser = new User(usernameET.getText().toString(),
                                    emailET.getText().toString(),
                                    pass,
                                    imgPath,
                                    Integer.parseInt(score));
            databaseReference.child("users").child(uid).setValue(editUser);
            databaseReference.child("places").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    for(DataSnapshot snapshot : datasnapshot.getChildren()){
                        Pictures pic = snapshot.getValue(Pictures.class);
                        if (pic != null && pic.getUsername().equals(nameForUpdate)) {
                            databaseReference
                                    .child("places")
                                    .child(Objects.requireNonNull(snapshot.getKey()))
                                    .child("username")
                                    .setValue(editUser.getUsername());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            sharedPreferences.edit().putString(USERNAME,usernameET.
                                                          getText().
                                                          toString()).apply();
            getProfilePage().updateRecycler();
            getParentFragmentManager().popBackStack();
        });

        addPicBtn.setOnClickListener(item -> choosePic());

        return view;
    }

    public void setFragment(ProfilePage profile){
        profilePage = profile;
    }

    public ProfilePage getProfilePage(){
        return this.profilePage;
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
                                    getBitmap(requireActivity().getContentResolver(),
                                            fileUri);
                            imageView.setImageBitmap(bitmap);
                        }
                        catch (IOException err){
                            err.printStackTrace();
                        }
                    }
                }
            });

    private String uploadImage(){
        String imgUID = UUID.randomUUID().toString();
        StorageReference ref =
                storageReference.child(
                        "images/" + imgUID);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                })
                .addOnFailureListener(e -> Log.e("not ok","Not working"));
        return imgUID;
    }
}