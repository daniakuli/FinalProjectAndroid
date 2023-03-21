package com.example.finalprojectandroid.DataBase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.example.finalprojectandroid.Models.Pictures;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static String getImageAsBase64(Bitmap img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap parseBitmapFromBase64(String base64Img) {
        byte[] bytes = Base64.decode(base64Img,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static void loadImageFromPictures(Pictures pictures, ImageView imageView){
        if(pictures.getImageContent() != null && !pictures.getImageContent().isEmpty()){
            Bitmap imgBitmap = parseBitmapFromBase64(pictures.getImageContent());
            imageView.setImageBitmap(imgBitmap);
        }else {
            Picasso.get().load(pictures.getImage()).into(imageView);
        }
    }
}
