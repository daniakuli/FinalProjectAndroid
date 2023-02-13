package com.example.finalprojectandroid.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroid.Adapters.ProfileAdapter;
import com.example.finalprojectandroid.OnGetDataListener;
import com.example.finalprojectandroid.OnItemClickListener;
import com.example.finalprojectandroid.Pictures;
import com.example.finalprojectandroid.R;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment {
    private RecyclerView pRecyclerView;
    private ImageView pProfileImage;
    private ProfileAdapter profileAdapter;
    private Pictures pictures;
    private List<Pictures> picturesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //pProfileImage     = view.findViewById(R.id.other_pro_pic);
        pRecyclerView = view.findViewById(R.id.recycler_view);

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
                        /*DialogFragment dialogFragment = new ImageDialogFragment();
                        Bundle args = new Bundle();
                        args.putString("imageUrl", picturesList.get(pos).getImage());
                        args.putString("text1", picturesList.get(pos).getCountry());
                        args.putString("text2", picturesList.get(pos).getCity());
                        args.putInt("pos",pos);
                        dialogFragment.setArguments(args);
                        ((ImageDialogFragment) dialogFragment).setFragment(ProfilePage.this);
                        dialogFragment.showNow(getParentFragmentManager(), "ImageDialog");*/
                    }
                });
            }
        }, false);


        return view;
    }
}
