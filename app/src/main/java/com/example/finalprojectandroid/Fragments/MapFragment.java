package com.example.finalprojectandroid.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalprojectandroid.Interfaces.MapAPI;
import com.example.finalprojectandroid.Models.LocationResponse;
import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private static final String USERNAME = "username";
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        String username = sharedPreferences.getString(USERNAME, "");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        Bundle args = getArguments();
        if (args != null) {
            List<Pictures> myArrayList = (List<Pictures>) args.getSerializable("picList");
            for(Pictures pic : myArrayList){
                addMarkerToMap(pic.getCountry(),
                               pic.getCity());
            }
            LatLng sydney = new LatLng(-33.852, 151.211);
            googleMap.addMarker(new MarkerOptions()
                    .position(sydney)
                    .title("Marker in Sydney"));
        }
        }

    private void addMarkerToMap(String country, String city) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MapAPI service = retrofit.create(MapAPI.class);

        Call<LocationResponse> call = service.geocode(
                city + ", " + country,
                "AIzaSyBXEuuUedMpkqafQMuhHSEUrSZd5uxZ-vI"
        );

        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    LocationResponse geocodingResponse = response.body();
                    if (geocodingResponse != null && !geocodingResponse.getResults().isEmpty()) {
                        LocationResponse.Result result = geocodingResponse.getResults().get(0);
                        LocationResponse.Result.Geometry.LatLng location = result.getGeometry().getLocation();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng));
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}