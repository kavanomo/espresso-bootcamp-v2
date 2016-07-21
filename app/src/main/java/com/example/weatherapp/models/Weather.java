package com.example.weatherapp.models;

@SuppressWarnings("unused")
public class Weather {
    private int id;
    private String main;
    private String description;

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }
}
