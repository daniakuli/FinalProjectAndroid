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
import androidx.navigation.Navigation;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.Models.RoomDatabaseManager;
import com.example.finalprojectandroid.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.finalprojectandroid.databinding.FragmentEditProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EditProfile extends Fragment {
    private SharedPreferences sharedPreferences;
    private StorageReference storageReference;
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String FULL_EMAIL = "fullEmail";
    private static final String SCORE = "0";
    private String uid = "";
    private String imgPath = "";
    private String nameForUpdate;
    private String score;
    private Uri fileUri;
    private Boolean picChanged = false;
    private ProfilePage profilePage;
    private FragmentEditProfileBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
                //.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");//.child("users");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");


        sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);
        nameForUpdate = sharedPreferences.getString(FULL_EMAIL, "");
        score = sharedPreferences.getString(SCORE,"");
        binding.editEmail.setEnabled(false);

        RoomDatabaseManager roomDatabaseManager = new RoomDatabaseManager(getActivity());

        List<User> userData = roomDatabaseManager.getUserByEmail(nameForUpdate);
        binding.editEmail.setEnabled(false);
        binding.editEmail.setText(userData.get(0).getEmail());
        binding.editUsername.setText(userData.get(0).getUsername());
        Picasso.get().load(userData.get(0).getImage()).into(binding.editPic);
        imgPath = userData.get(0).getImage();

        binding.editBackBtn.setOnClickListener(item -> getParentFragmentManager().popBackStack());

        binding.editSave.setOnClickListener(item -> {
            if(picChanged){
                imgPath = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                Toast.makeText(getActivity(),
                        "Profile picture Changed",
                              Toast.LENGTH_LONG).show();
            }

            User editUser = new User(binding.editUsername.getText().toString(),
                                    binding.editEmail.getText().toString(),
                                    imgPath,
                                    Integer.parseInt(score));
            databaseReference.child("users").child(sharedPreferences.getString(EMAIL, "")).setValue(editUser);
            databaseReference.child("places").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    for(DataSnapshot snapshot : datasnapshot.getChildren()){
                        Pictures pic = snapshot.getValue(Pictures.class);
                        if (pic != null && pic.getEmail().equals(nameForUpdate)) {
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
            sharedPreferences.edit().putString(USERNAME,binding.editUsername.
                                                          getText().
                                                          toString()).apply();

            roomDatabaseManager.updateUser(editUser);

            //NavController navController = Navigation.findNavController(requireView());
            //navController.popBackStack();
            Navigation.findNavController(view).popBackStack();
        });

        binding.editProfilePicbtn.setOnClickListener(item -> choosePic());

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
                            binding.editPic.setImageBitmap(bitmap);
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
//                    profilePage.refreshImage();
                })
                .addOnFailureListener(e ->
                        Log.e("not ok","Not working"));
        return imgUID;
    }
}