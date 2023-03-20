package com.example.finalprojectandroid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroid.Adapters.ProfileAdapter;
import com.example.finalprojectandroid.Activites.Login;
import com.example.finalprojectandroid.Interfaces.UsersDao;
import com.example.finalprojectandroid.Models.AppDatabase;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.Models.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.finalprojectandroid.databinding.FragmentHomeBinding;
import com.example.finalprojectandroid.databinding.FragmentProfilePageBinding;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
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
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        //DatabaseReference users = firebaseDatabase.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("users");


        /*databaseReference.child("users").child(sharedPreferences.getString(EMAIL, "")).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DataSnapshot> task) {
                   if (task.isSuccessful()) {
                       User user = task.getResult().getValue(User.class);
                       assert user != null;
                       Log.d("userName", user.getUsername());
                       Picasso.get().load(user.getImage()).into(binding.imageProfile);
                   }
               }
           });*/

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                UsersDao userDao = db.usersDao();
                String email = sharedPreferences.getString(FULL_EMAIL, "");
                User user = userDao.getUserByEmail(email);
                List<User> userList = userDao.getAllUsers();
                binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                binding.recyclerView.setHasFixedSize(true);
                profileAdapter = new ProfileAdapter();
                binding.recyclerView.setAdapter(profileAdapter);
                //Log.d("yes",user.getEmail());
                /*requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String pictureUrl = user.getImage();
                        Picasso.get()
                                .load(pictureUrl)
                                .into(binding.imageProfile);
                    }
                });*/
                //Picasso.get().load(user.getImage()).into(binding.imageProfile);
                //Log.d("EMAIL", email);
                // Handle the retrieved user object here
            }
        });

        binding.profileUserName.setText(sharedPreferences.getString(USERNAME,""));
        binding.scoreUserName.setText(sharedPreferences.getString(SCORE,""));

        //binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        //binding.recyclerView.setHasFixedSize(true);

        picturesList = new ArrayList<>();
        Pictures pictures = new Pictures();

        /*pictures.getData(requireActivity(), picList -> {
            picturesList = picList;
            profileAdapter = new ProfileAdapter();//picturesList);
            binding.recyclerView.setAdapter(profileAdapter);
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
        }, true);*/


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
    public void updateData() {
        profileAdapter.addData(picturesList);
    }
}
