package com.example.finalprojectandroid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.finalprojectandroid.Adapters.ProfileAdapter;
import com.example.finalprojectandroid.Activites.Login;
import com.example.finalprojectandroid.Interfaces.UsersDao;
import com.example.finalprojectandroid.Models.AppDatabase;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.Models.RoomDatabaseManager;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Models.User;

import com.example.finalprojectandroid.databinding.FragmentProfilePageBinding;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfilePage extends Fragment{

    private static final String USERNAME = "username";
    private static final String FULL_EMAIL = "fullEmail";
    private static final String SCORE = "0";
    private SharedPreferences sharedPreferences;
    private ProfileAdapter profileAdapter;
    private List<Pictures> picturesList;
    private FragmentProfilePageBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfilePageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //DatabaseReference databaseReference = firebaseDatabase.getReference();

        String email = sharedPreferences.getString(FULL_EMAIL, "");
        /*ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                UsersDao userDao = db.usersDao();
                User user = userDao.getUserByEmail(email);
                List<User> userList = userDao.getAllUsers();
            }
        });*/

        binding.profileUserName.setText(sharedPreferences.getString(USERNAME,""));
        binding.scoreUserName.setText(sharedPreferences.getString(SCORE,""));

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        binding.recyclerView.setHasFixedSize(true);
        profileAdapter = new ProfileAdapter();
        binding.recyclerView.setAdapter(profileAdapter);

        RoomDatabaseManager roomDatabaseManager =new RoomDatabaseManager(getActivity());

        roomDatabaseManager.getThisUserPictures(email).observe(getViewLifecycleOwner(), list -> {
            profileAdapter.setData(list);
            picturesList = list;
        });


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


        binding.logout.setOnClickListener(item -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().apply();

            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        });

        binding.editProfile.setOnClickListener(item -> {
            Navigation.findNavController(view).navigate(R.id.action_profilePage_to_editPage);

        });

        binding.showMap.setOnClickListener(item ->{
            Pictures[] picturesArray = picturesList.toArray(new Pictures[picturesList.size()]);
            ProfilePageDirections.ActionProfilePageToMapPage action
                    = ProfilePageDirections.actionProfilePageToMapPage(picturesArray);
            Navigation.findNavController(view).navigate(action);
        });
        return view;
    }

    public void updateRecycler(){
        profileAdapter.notifyDataSetChanged();
    }
    public void updateData(Pictures pic, int pos) {
        profileAdapter.notifyItemChanged(pos,pic);
    }
}
