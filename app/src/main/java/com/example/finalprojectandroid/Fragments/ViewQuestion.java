package com.example.finalprojectandroid.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.databinding.ViewQuestionBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewQuestion extends Fragment {

    private ViewQuestionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = ViewQuestionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/");


        databaseReference.child("places").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pictures pictures = snapshot.getValue(Pictures.class);
                    Picasso.get().load(pictures.getImage()).resize(450,0).into(binding.imageView);
                    binding.question.setText("Where is the picture taken?");
                    binding.radioButton1.setText(pictures.getCountry() + ", " + pictures.getCity());
                    binding.radioButton2.setText("Jordan");
                    binding.radioButton3.setText("Finland");
                    binding.radioButton4.setText("Germany");
                    break;
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

