package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Coordinates {
    @SerializedName("lon")
    private Double longitude;
    @SerializedName("lat")
    private Double latitude;

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
