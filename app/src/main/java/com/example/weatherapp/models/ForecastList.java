package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ForecastList {

    @SerializedName("dt")
    private Long dateTime;
    private Temperature temp;
    private List<Weather> weather;

    private Double pressure;
    private int humidity;
    private double speed;
    private int deg;

    public int getDeg() {
        return deg;
    }

    public Double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getSpeed() {
        return speed;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public Temperature getTemp() {
        return temp;
    }

    public List<Weather> getWeather() {
        return weather;
    }

}
