package com.example.finalprojectandroid.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class ViewQuestion extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_question, container, false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView questionText = view.findViewById(R.id.question);
        Button saveButton = view.findViewById(R.id.save_answer);
        RadioButton radioButton1 = view.findViewById(R.id.radio_button_1);
        RadioButton radioButton2 = view.findViewById(R.id.radio_button_2);
        RadioButton radioButton3 = view.findViewById(R.id.radio_button_3);
        RadioButton radioButton4 = view.findViewById(R.id.radio_button_4);

        databaseReference.child("places").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pictures pictures = snapshot.getValue(Pictures.class);
                    Picasso.get().load(pictures.getImage()).resize(450,0).into(imageView);
                    questionText.setText("Where is the picture taken?");
                    radioButton1.setText(pictures.getCountry() + ", " + pictures.getCity());
                    radioButton2.setText("Jordan");
                    radioButton3.setText("Finland");
                    radioButton4.setText("Germany");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}

