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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.example.finalprojectandroid.Models.CityWiki;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.Models.User;
import com.example.finalprojectandroid.Models.WikipediaModel;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.databinding.ViewQuestionBinding;
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

    private ViewQuestionBinding binding;

    private SharedPreferences sharedPreferences;
    private static final String SCORE = "0";
    private static final String USERNAME = "username";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ViewQuestionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.progressBar1.setVisibility(View.VISIBLE);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");


        Bundle args = getArguments();
        String otherUser = "";
        String otherImageUrl = "";
        if (args != null) {
            otherUser = args.getString("username");
            otherImageUrl = args.getString("imgUrl");
        }

        String finalOtherUser = otherUser;
        String finalOtherImageUrl = otherImageUrl;

        binding.saveAnswer.setOnClickListener(item -> {
            sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);
            String score = sharedPreferences.getString(SCORE,"");
            if(binding.radioButton1.isChecked()) {
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

                    Picasso.get().load(pictures.getImage()).resize(450, 0).into(binding.imageView);
                    binding.progressBar1.setVisibility(View.GONE);
                    binding.question.setText("Where is the picture taken?");
                    binding.radioButton1.setText(pictures.getCountry() + ", " + pictures.getCity());
                    binding.radioButton2.setText("Russia, Moscow");
                    binding.radioButton3.setText("Finland, Helsinki");
                    binding.radioButton4.setText("Germany, Berlin");
                    LiveData<CityWiki> data = WikipediaModel.instance.getWikiData(pictures.getCity());
                    data.observe(getViewLifecycleOwner(), kek -> {
                        Log.d("TAG", "extract: " + kek.getExtract());
                    });
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        binding.saveAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        return view;
    }
}