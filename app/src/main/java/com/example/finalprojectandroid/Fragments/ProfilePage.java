package com.example.finalprojectandroid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroid.Adapters.ProfileAdapter;
import com.example.finalprojectandroid.Login;
import com.example.finalprojectandroid.OnGetDataListener;
import com.example.finalprojectandroid.OnItemClickListener;
import com.example.finalprojectandroid.Pictures;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfilePage extends Fragment{

    private static final String USERNAME = "username";
    private static final String SCORE = "0";
    private RecyclerView pRecyclerView;
    private ImageView pProfileImage;
    private TextView pUserName, pScore;
    private Button pEditButton, pLogOut;
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase firebaseDatabase;
    private ProfileAdapter profileAdapter;
    private Pictures pictures;
    private List<Pictures> picturesList;

    private String uid = "";
    private String[] countries;
    private String[] cities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        sharedPreferences = getActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);
        pProfileImage     = view.findViewById(R.id.image_profile);
        pRecyclerView     = view.findViewById(R.id.recycler_view);
        pUserName         = view.findViewById(R.id.profile_user_name);
        pScore            = view.findViewById(R.id.score_user_name);
        pLogOut           = view.findViewById(R.id.logout);
        pEditButton       = view.findViewById(R.id.edit_profile);

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference users = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("users");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user.getUsername()
                            .equals(sharedPreferences.getString(USERNAME,""))) {
                        Picasso.get().load(user.getImage()).into(pProfileImage);
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

        pRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        pRecyclerView.setHasFixedSize(true);

        picturesList = new ArrayList<>();
        pictures = new Pictures();

        pictures.getData(getActivity(),new OnGetDataListener() {
            @Override
            public void onSuccess(List<Pictures> picList) {
                picturesList = picList;
                profileAdapter = new ProfileAdapter(getActivity(),
                                                    picturesList);
                pRecyclerView.setAdapter(profileAdapter);
                profileAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int pos) {
                        DialogFragment dialogFragment = new ImageDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("imageUrl", picturesList.get(pos).getImage());
                        args.putString("text1", picturesList.get(pos).getCountry());
                        args.putString("text2", picturesList.get(pos).getCity());
                        args.putInt("pos",pos);
                        dialogFragment.setArguments(args);
                        ((ImageDialogFragment) dialogFragment).setFragment(ProfilePage.this);
                        dialogFragment.showNow(getParentFragmentManager(), "ImageDialog");
                    }
                });
            }
        }, true);


        pLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().apply();

                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        pEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfile ep = new EditProfile();
                ((EditProfile)ep).setFragment(ProfilePage.this);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container,ep)
                        .addToBackStack(null)
                        .commit();
            }
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
