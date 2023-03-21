package com.example.finalprojectandroid.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.finalprojectandroid.Interfaces.OnGetDataListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pictures")
public class Pictures implements Parcelable {
    private static final String USERNAME = "username";

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "imgUrl")
    private String image;
    @ColumnInfo(name = "country")
    private String country;
    @ColumnInfo(name = "city")
    private String city;

    public Pictures() {
    }

    public Pictures(String email, String image, String country, String city) {
        this.email = email;
        this.image = image;
        this.country = country;
        this.city = city;
    }

    protected Pictures(Parcel in) {
        email = in.readString();
        image = in.readString();
        country = in.readString();
        city = in.readString();
    }

    public static final Creator<Pictures> CREATOR = new Creator<Pictures>() {
        @Override
        public Pictures createFromParcel(Parcel in) {
            return new Pictures(in);
        }

        @Override
        public Pictures[] newArray(int size) {
            return new Pictures[size];
        }
    };

    public int getId() { return id; }

    public String getImage() {
        return image;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() { return email; }

    public void setId(int id) { this.id = id; }

    public void setEmail(String email) { this.email = email; }

    public void setImage(String image) { this.image = image; }

    public void setCountry(String country) { this.country = country; }

    public void setCity(String city) { this.city = city; }

    public void getData(Context context, OnGetDataListener listener, Boolean isProfile) {
        List<Pictures> picturesList = new ArrayList<>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("app_pref", Context.MODE_PRIVATE);

        String name = sharedPreferences.getString(USERNAME,"");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReferenceFromUrl("https://finalprojectandroind-default-rtdb.firebaseio.com/").child("places");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Pictures pic = snapshot.getValue(Pictures.class);
                    if (pic != null) {
                        if(isProfile && pic.email.equals(name)) {
                            picturesList.add(pic);
                        }
                        else if(!isProfile && !pic.email.equals(name)){
                            picturesList.add(pic);
                        }
                    }
                }
                listener.onSuccess(picturesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(image);
        parcel.writeString(country);
        parcel.writeString(city);
    }
}
