package com.example.finalprojectandroid.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalprojectandroid.Models.Pictures;
import com.example.finalprojectandroid.R;
import com.example.finalprojectandroid.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;



public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    Pictures[] pictures;
    private static final String USERNAME = "username";
    private SharedPreferences sharedPreferences;
    private FragmentMapBinding binding;
    private FusedLocationProviderClient fusedLocationClient;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPreferences = requireActivity().getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        //String username = sharedPreferences.getString(USERNAME, "");

        if(getArguments() != null) {
            pictures = (Pictures[]) getArguments().getParcelableArray("pictures");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return view;
    }


    @SuppressLint("MissingPermission")
    private final ActivityResultLauncher<String> reqLocPer =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                                      result -> {
                                          if (result) {
                                              // Permission granted, try to get the current location again
                                              if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                  Log.d("Permissions", "Grunted");
                                                  fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                                      @Override
                                                      public void onSuccess(Location location) {
                                                          if (location != null) {
                                                              Location currentLocation = location;
                                                              map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 14));
                                                          }
                                                          else{
                                                              Log.e("Loc","Something wrong");
                                                          }
                                                      }
                                                  });
                                              }
                                          }
                                          else{
                                              Toast.makeText(getActivity(), "This feature requires location permission", Toast.LENGTH_SHORT).show();
                                              NavHostFragment.findNavController(this).popBackStack();
                                          }
                                      });

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Permissions", "Grunted");
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if(location != null){
                    LatLng myLocation = new LatLng(location.getLatitude(),
                                                   location.getLongitude());
                    Log.d("Loc", location.getLatitude() + ", " + location.getLongitude());
                    map.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title(myLocation.toString()));
                    map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    map.animateCamera(CameraUpdateFactory.zoomTo(12f));
                }
                else{
                    Log.e("Loc", "No location found");
                }
            });
        }
        else{
            Log.e("Permission", "No Permissions");
            reqLocPer.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        for(Pictures pic : pictures){
            LatLng latLng = getLocationFromAddress(pic.getCity() + "," + pic.getCountry());
            map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(pic.getCity() + ", " + pic.getCountry())
                    .snippet(pic.getCity() + ", " + pic.getCountry() + ","  + pic.getImage())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        map.setOnMarkerClickListener(this);
        }

    private LatLng getLocationFromAddress(String address){
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                double lat = addresses.get(0).getLatitude();
                double lng = addresses.get(0).getLongitude();
                return new LatLng(lat, lng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        Log.d("check","check");
        Marker currentMarker = marker;
        String snippet = marker.getSnippet();
        if (snippet != null) {
            String[] parts = snippet.split(",");
            if (parts.length >= 3) {
                String imgUrl = parts[2];
                //StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);

                Log.d("imgURL", imgUrl);
                Picasso.get().load(imgUrl).resize(130,130).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (currentMarker != null) {
                            currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Log.e("Picasso", "Failed to load image for marker", e);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // do nothing
                    }
                });
            }
        }
        return false;
    }
}
/*
StorageReference storageReference = FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child("images/" + imgUrl);
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("checking", "enter?");
                        Picasso.get().load(imgUrl).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                if (currentMarker != null) {
                                    currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                Log.e("Picasso", "Failed to load image for marker", e);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                // do nothing
                            }
                        });
                    }
                });

 */