package com.example.finalprojectandroid.DataBase;

public class Pair<Boolean, Uri, ImageView> {
    public final Boolean picChanged;
    public final Uri fileUri;
    public final ImageView img;

    public Pair(Boolean picChanged, Uri fileUri, ImageView img){
        this.picChanged = picChanged;
        this.fileUri = fileUri;
        this.img = img;
    }
}
