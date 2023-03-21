package com.example.finalprojectandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.finalprojectandroid.Adapters.ProfileAdapter;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.Models.RoomDatabaseManager;
import com.example.finalprojectandroid.databinding.FragmentHomeBinding;

import java.util.List;

public class HomePage extends Fragment {
    private ProfileAdapter profileAdapter;
    private List<Pictures> picturesList;
    private SharedPreferences sharedPreferences;
    private static final String FULL_EMAIL = "fullEmail";
    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        binding.recyclerView.setHasFixedSize(true);
        profileAdapter = new ProfileAdapter();
        binding.recyclerView.setAdapter(profileAdapter);

        sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(FULL_EMAIL, "");

        RoomDatabaseManager roomDatabaseManager = new RoomDatabaseManager(getActivity());

        roomDatabaseManager.getAllOtherPictures(email).observe(getViewLifecycleOwner(), list -> {
            profileAdapter.setData(list);
            picturesList = list;
        });
            profileAdapter.setOnItemClickListener(pos -> {
                Pictures pic = picturesList.get(pos);
                HomePageDirections.ActionHomePageToViewQuestion action
                        = HomePageDirections.actionHomePageToViewQuestion(pic.getEmail(),
                                                                          pic.getImage());
                Navigation.findNavController(view).navigate(action);
            });



        return view;
    }
}
