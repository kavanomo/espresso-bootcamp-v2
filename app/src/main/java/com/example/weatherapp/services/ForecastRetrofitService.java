package com.example.weatherapp.services;

import com.example.weatherapp.BuildConfig;
import com.example.weatherapp.models.Forecast;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ForecastRetrofitService {
    @GET("data/2.5/forecast/daily?mode=json&units=metric&cnt=7&APPID="+ BuildConfig.API_KEY)
    Call<Forecast> getForecast(@Query("q") String zip);
}
