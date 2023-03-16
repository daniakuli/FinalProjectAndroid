package com.example.finalprojectandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.finalprojectandroid.Models.User;
import com.example.finalprojectandroid.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class ViewQuestion extends Fragment {


    private SharedPreferences sharedPreferences;
    private static final String SCORE = "0";
    private static final String USERNAME = "username";

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

        Bundle args = getArguments();
        String otherUser = "";
        String otherImageUrl = "";
        if (args != null) {
             otherUser = args.getString("username");
             otherImageUrl = args.getString("imgUrl");
        }

        String finalOtherUser = otherUser;
        String finalOtherImageUrl = otherImageUrl;

        saveButton.setOnClickListener(item -> {
            sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);
            String score = sharedPreferences.getString(SCORE,"");
                if(radioButton1.isChecked()) {
                    Integer intScore = Integer.parseInt(score);
                    intScore += 10;
                    sharedPreferences.edit().putString(SCORE, intScore.toString()).apply();

                    Integer finalIntScore = intScore;
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if(user.getUsername().equals(sharedPreferences.getString(USERNAME,""))) {
                                    databaseReference.child("users").child(snapshot.getKey()).child("score").setValue(finalIntScore);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            getParentFragmentManager().popBackStack();
        });


        databaseReference.child("places").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pictures pictures = snapshot.getValue(Pictures.class);
//                    if(pictures.getUsername().equals(finalOtherUser) && pictures.getImage().equals(finalOtherImageUrl)) {
                        Picasso.get().load(pictures.getImage()).resize(450, 0).into(imageView);
                        questionText.setText("Where is the picture taken?");
                        radioButton1.setText(pictures.getCountry() + ", " + pictures.getCity());
                        radioButton2.setText("Russia, Moscow");
                        radioButton3.setText("Finland, Helsinki");
                        radioButton4.setText("Germany, Berlin");
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        return view;
    }
}

