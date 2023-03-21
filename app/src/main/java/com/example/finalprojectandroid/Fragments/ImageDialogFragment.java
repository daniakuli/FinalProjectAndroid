package com.example.finalprojectandroid.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalprojectandroid.DataBase.ImageUtils;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.databinding.FragmentImageDialogBinding;
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

public class ImageDialogFragment extends DialogFragment {
    private static final String USERNAME = "username";
    private SharedPreferences sharedPreferences;

    private String imageUrl;
    private String imgAsBase64 = "";
    private int pos;
    private ProfilePage profilePage;
    private Boolean picChanged = false;
    private Uri fileUri;
    private StorageReference storageReference;
    private FragmentImageDialogBinding binding;


    public ImageDialogFragment newInstance(String imageUrl, String country, String city) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        args.putString("country", country);
        args.putString("city", city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentImageDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");

        binding.description.setText(getDescription());
        binding.countryET.setEnabled(false);
        binding.cityET.setEnabled(false);
        binding.changePicture.hide();

        imageUrl = requireArguments().getString("imageUrl");
        String country = requireArguments().getString("country");
        String city = requireArguments().getString("city");
        pos = requireArguments().getInt("pos");

        Picasso.get().load(imageUrl).into(binding.imageView);

        this.binding.countryET.setText(country);
        this.binding.cityET.setText(city);

        binding.btnEdit.setOnClickListener(item -> {
            this.binding.countryET.setEnabled(true);
            this.binding.countryET.setPaintFlags(this.binding.countryET.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            this.binding.cityET.setEnabled(true);
            this.binding.cityET.setPaintFlags(this.binding.cityET.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            binding.btnSave.setVisibility(View.VISIBLE);
            binding.btnEdit.setVisibility(View.GONE);
            binding.changePicture.show();
        });

        binding.changePicture.setOnClickListener(item -> choosePic());

        binding.btnSave.setOnClickListener(item -> {
            if(picChanged){
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                Toast.makeText(getActivity(),
                        "Profile picture Changed",
                        Toast.LENGTH_LONG).show();
            }
            String name = sharedPreferences.getString(USERNAME,"");

            Pictures pic = new Pictures(name,imageUrl,
                                        this.binding.countryET.getText().toString(),
                                        this.binding.cityET.getText().toString(), imgAsBase64);
            changeData(pic,pos);

            getProfilePage().updateData();

            dismiss();
        });
        return view;
    }

    private String getDescription() {
        return "";
    }

    public void setFragment(ProfilePage profile){
        profilePage = profile;
    }

    public ProfilePage getProfilePage(){
        return this.profilePage;
    }

    private void changeData(Pictures pic, int pos) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference pictures = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("places");

        pictures.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                int count = 0;
                for(DataSnapshot snapshot: datasnapshot.getChildren()){
                    if(count == pos){
                        pictures
                                .child(Objects.requireNonNull(snapshot.getKey()))
                                .setValue(pic);
                    }
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                                    getBitmap(requireActivity().getContentResolver(),
                                            fileUri);
                            binding.imageView.setImageBitmap(bitmap);

                            imgAsBase64 = ImageUtils.getImageAsBase64(bitmap);
                        }
                        catch (IOException err){
                            err.printStackTrace();
                        }
                    }
                }
            });

    private String uploadImage(){
        String imgUri = UUID.randomUUID().toString();
        StorageReference ref =
                storageReference.child(
                        "images/" + imgUri);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                })
                .addOnFailureListener(e -> Log.e("not ok","Not working"));
        return imgUri;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.75);
            int height = (int)(getResources().getDisplayMetrics().heightPixels * 0.75);
            dialog.getWindow().setLayout(width, height);
        }
    }
}
