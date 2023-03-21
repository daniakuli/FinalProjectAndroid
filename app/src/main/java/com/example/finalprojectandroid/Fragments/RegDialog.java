package com.example.finalprojectandroid.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.finalprojectandroid.R;

public class RegDialog extends Dialog {
    private EditText country, city;
    private ImageView imageView;

    public RegDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.register_dialog);

        country = findViewById(R.id.reg_country);
        city = findViewById(R.id.reg_city);
        Button saveBtn = findViewById(R.id.btn_save);
        imageView = findViewById(R.id.reg_image_view);

        saveBtn.setOnClickListener(view -> dismiss());
    }

    public void setImage(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    public void setCountry(String countryReg){
        country.setText(countryReg);
    }

    public void setCity(String cityReg){
        city.setText(cityReg);
    }
}
