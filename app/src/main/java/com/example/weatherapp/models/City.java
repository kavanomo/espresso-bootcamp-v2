package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class City {
    private String name;
    private String country;

    @SerializedName("coord")
    private Coordinates coordinates;

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
