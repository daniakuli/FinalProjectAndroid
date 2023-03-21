package com.example.finalprojectandroid.DataBase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static String getImageAsBase64(Bitmap img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }
}
