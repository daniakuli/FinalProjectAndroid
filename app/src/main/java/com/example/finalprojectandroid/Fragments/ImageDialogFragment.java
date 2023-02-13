package com.example.finalprojectandroid.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

import com.example.finalprojectandroid.Pictures;
import com.example.finalprojectandroid.R;
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

public class ImageDialogFragment extends DialogFragment {
    private ImageView imageView;
    private EditText country;
    private EditText city;
    private FloatingActionButton floatingActionButton;
    private Button editButton, saveButton;
    private String imageUrl, text1 ,text2;
    private int pos;
    private ProfilePage profilePage;
    private Boolean picChanged = false;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    private FirebaseDatabase firebaseDatabase;



    public static ImageDialogFragment newInstance(String imageUrl, String text1, String text2) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        args.putString("text1", text1);
        args.putString("text2", text2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_dialog, container, false);
        imageView = view.findViewById(R.id.image_view);
        country = view.findViewById(R.id.text_view_1);
        city = view.findViewById(R.id.text_view_2);
        editButton = view.findViewById(R.id.btn_edit);
        saveButton = view.findViewById(R.id.btn_save);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://finalprojectandroind.appspot.com/");

        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        country.setEnabled(false);
        city.setEnabled(false);
        floatingActionButton.hide();

        imageUrl = getArguments().getString("imageUrl");
        text1 = getArguments().getString("text1");
        text2 = getArguments().getString("text2");
        pos = getArguments().getInt("pos");

        Picasso.get().load(imageUrl).into(imageView);

        country.setText(text1);
        city.setText(text2);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country.setEnabled(true);
                country.setPaintFlags(country.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                city.setEnabled(true);
                city.setPaintFlags(city.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                saveButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                floatingActionButton.show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {choosePic();}
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(picChanged){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/finalprojectandroind.appspot.com/o/images%2F" + uploadImage() + "?alt=media";
                    Toast.makeText(getActivity(),
                            "Profile picure Changed",
                            Toast.LENGTH_LONG).show();
                }
                Pictures pic = new Pictures("",imageUrl,
                                            country.getText().toString(),
                                            city.getText().toString());
                changeData(pic,pos);

                getProfilePage().updateData(pic,pos);

                dismiss();
            }
        });
        return view;
    }

    public void setFragment(ProfilePage profile){
        profilePage = profile;
    }

    public ProfilePage getProfilePage(){
        return this.profilePage;
    }

    private void changeData(Pictures pic, int pos) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference pictures = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("places");

        pictures.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                int count = 0;
                Log.d("pos", "pos: " + datasnapshot.getChildrenCount());
                for(DataSnapshot snapshot: datasnapshot.getChildren()){
                    if(count == pos){
                        Log.d("pos", "pos: " + pos);
                        pictures
                                .child(snapshot.getKey())
                                .setValue(pic);
                    }
                    else{
                        Log.d("pos", "pos: " + pos);
                        Log.d("count", "count: " + count);
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
                        filePath = intent.getData();
                        try{
                            Bitmap bitmap = MediaStore.
                                    Images.
                                    Media.
                                    getBitmap(getActivity().getContentResolver(),
                                            filePath);
                            imageView.setImageBitmap(bitmap);
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
