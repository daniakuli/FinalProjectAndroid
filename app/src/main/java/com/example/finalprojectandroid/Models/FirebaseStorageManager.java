package com.example.finalprojectandroid.Models;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class FirebaseStorageManager {
    private final FirebaseStorage firebaseStorage;

    public FirebaseStorageManager(){
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public String uploadImage(Uri fileUri){
        String imgUID = UUID.randomUUID().toString() + ".png";
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference ref =
                storageReference.child(
                        "images/" + imgUID);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                })
                .addOnFailureListener(e -> Log.e("not ok","Not working"));
        return imgUID;
    }

    public void downloadImage(String name, ImageView imageView){
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference reference = storageReference.child("images/" + name);
    }

    public void getOtherUsersPictures(String userId, OnSuccessListener<Uri> successListener, OnFailureListener failureListener) {
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference usersRef = storageRef.child("images/");

        usersRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference userRef : listResult.getPrefixes()) {
                        String userPath = userRef.getPath();
                        String[] pathParts = userPath.split("/");
                        String otherUserId = pathParts[pathParts.length - 2];

                        if (!otherUserId.equals(userId)) {
                            StorageReference userPicturesRef = userRef.child("images/");

                            userPicturesRef.listAll()
                                    .addOnSuccessListener(picturesListResult -> {
                                        for (StorageReference item : picturesListResult.getItems()) {
                                            item.getDownloadUrl().addOnSuccessListener(successListener).addOnFailureListener(failureListener);
                                        }
                                    })
                                    .addOnFailureListener(failureListener);
                        }
                    }
                })
                .addOnFailureListener(failureListener);
    }

}
