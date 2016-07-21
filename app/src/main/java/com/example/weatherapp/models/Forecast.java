package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Forecast {
    private City city;
    @SerializedName("list")
    private ArrayList<ForecastList> forecastList;
    private String cod;
    private String message;

    public City getCity() {
        return city;
    }

    public ArrayList<ForecastList> getForecastList() {
        return forecastList;
    }

    public String getCod() {
        return cod;
    }

    public String getMessage() {
        return message;
    }
}
