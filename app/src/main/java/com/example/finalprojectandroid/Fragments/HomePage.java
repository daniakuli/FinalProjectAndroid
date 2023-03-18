package com.example.finalprojectandroid.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroid.Adapters.ProfileAdapter;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.Models.RoomDatabaseManager;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment {
    //private RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;
    private LiveData<List<Pictures>> picturesList;
    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        binding.recyclerView.setHasFixedSize(true);

        Pictures pictures = new Pictures();

        //pictures.getData(requireActivity(), picList -> {
            //picturesList = picList;
            profileAdapter = new ProfileAdapter();//picturesList);
            binding.recyclerView.setAdapter(profileAdapter);
            RoomDatabaseManager roomDatabaseManager = new RoomDatabaseManager(requireContext());
            picturesList = roomDatabaseManager.getAllPictures();
        roomDatabaseManager.getAllPictures().observe(getViewLifecycleOwner(), profileAdapter:: addData);
            profileAdapter.setOnItemClickListener(pos -> {
                HomePageDirections.ActionHomePageToViewQuestion action
                        = HomePageDirections.actionHomePageToViewQuestion(picturesList.getValue().get(pos).getUsername(),
                                                                          picturesList.getValue().get(pos).getImage());
                Navigation.findNavController(view).navigate(action);
            });
        //}, false);



        return view;
    }
}
