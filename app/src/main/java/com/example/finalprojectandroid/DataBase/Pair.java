package com.example.finalprojectandroid.DataBase;

public class Pair<Boolean, Uri, ImageView> {
    public final Boolean picChanged;
    public final Uri filepath;
    public final ImageView img;

    public Pair(Boolean picChanged, Uri filepath, ImageView img){
        this.picChanged = picChanged;
        this.filepath = filepath;
        this.img = img;
    }
}
