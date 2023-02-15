package com.example.finalprojectandroid.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationResponse {
    @SerializedName("results")
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public static class Result {

        @SerializedName("geometry")
        private Geometry geometry;

        public Geometry getGeometry() {
            return geometry;
        }

        public static class Geometry {

            @SerializedName("location")
            private LatLng location;

            public LatLng getLocation() {
                return location;
            }

            public static class LatLng {

                @SerializedName("lat")
                private double latitude;

                @SerializedName("lng")
                private double longitude;

                public double getLatitude() {
                    return latitude;
                }

                public double getLongitude() {
                    return longitude;
                }
            }
        }
    }
}
