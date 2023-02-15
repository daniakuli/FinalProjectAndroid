package com.example.finalprojectandroid.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroid.Adapters.ProfileAdapter;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.R;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment {
    private RecyclerView recyclerView;
    private ProfileAdapter profileAdapter;
    private List<Pictures> picturesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setHasFixedSize(true);

        picturesList = new ArrayList<>();
        Pictures pictures = new Pictures();

        pictures.getData(requireActivity(), picList -> {
            picturesList = picList;
            profileAdapter = new ProfileAdapter(picturesList);
            recyclerView.setAdapter(profileAdapter);
            profileAdapter.setOnItemClickListener(pos -> {
            });
        }, false);


        return view;
    }
}
