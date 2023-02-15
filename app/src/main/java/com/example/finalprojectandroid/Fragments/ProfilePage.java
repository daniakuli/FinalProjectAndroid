package com.example.finalprojectandroid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroid.Adapters.ProfileAdapter;
import com.example.finalprojectandroid.Activites.Login;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfilePage extends Fragment{

    private static final String USERNAME = "username";
    private static final String SCORE = "0";
    private RecyclerView recyclerView;
    private ImageView imageView;
    private SharedPreferences sharedPreferences;
    private ProfileAdapter profileAdapter;
    private List<Pictures> picturesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);
        imageView = view.findViewById(R.id.image_profile);
        recyclerView = view.findViewById(R.id.recycler_view);
        TextView pUserName = view.findViewById(R.id.profile_user_name);
        TextView pScore = view.findViewById(R.id.score_user_name);
        Button logout = view.findViewById(R.id.logout);
        Button editBtn = view.findViewById(R.id.edit_profile);
        Button mapBtn = view.findViewById(R.id.show_map);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference users = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("users");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null && user.getUsername()
                            .equals(sharedPreferences
                                    .getString(USERNAME, ""))) {
                        Picasso.get().load(user.getImage()).into(imageView);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pUserName.setText(sharedPreferences.getString(USERNAME,""));
        pScore.setText(sharedPreferences.getString(SCORE,""));

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setHasFixedSize(true);

        picturesList = new ArrayList<>();
        Pictures pictures = new Pictures();

        pictures.getData(requireActivity(), picList -> {
            picturesList = picList;
            profileAdapter = new ProfileAdapter(picturesList);
            recyclerView.setAdapter(profileAdapter);
            profileAdapter.setOnItemClickListener(pos -> {
                ImageDialogFragment dialogFragment = new ImageDialogFragment();
                Bundle args = new Bundle();
                args.putString("imageUrl", picturesList.get(pos).getImage());
                args.putString("country", picturesList.get(pos).getCountry());
                args.putString("city", picturesList.get(pos).getCity());
                args.putInt("pos",pos);
                dialogFragment.setArguments(args);
                dialogFragment.setFragment(ProfilePage.this);
                dialogFragment.showNow(getParentFragmentManager(), "ImageDialog");
            });
        }, true);


        logout.setOnClickListener(item -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().apply();

            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        });

        editBtn.setOnClickListener(item -> {
            EditProfile ep = new EditProfile();
            ((EditProfile)ep).setFragment(ProfilePage.this);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,ep)
                    .addToBackStack(null)
                    .commit();
        });

        mapBtn.setOnClickListener(item ->{
            MapFragment mapFragment = new MapFragment();

            Bundle args = new Bundle();
            args.putSerializable("picList", (Serializable) picturesList);
            mapFragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,mapFragment)
                        .addToBackStack(null)
                        .commit();

        });

        return view;
    }

    public void updateRecycler(){
        profileAdapter.notifyDataSetChanged();
    }
    public void updateData(Pictures pic, int pos) {
        profileAdapter.addData(pic,pos);
    }
}
