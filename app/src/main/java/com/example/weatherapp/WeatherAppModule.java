package com.example.weatherapp;

import com.example.weatherapp.activities.MainActivity;
import com.example.weatherapp.adapters.ForecastAdapter;
import com.example.weatherapp.data.WeatherDBHelper;
import com.example.weatherapp.data.WeatherProvider;
import com.example.weatherapp.fragments.DetailsActivityFragment;
import com.example.weatherapp.fragments.MainActivityFragment;
import com.example.weatherapp.fragments.PreferenceFragment;
import com.example.weatherapp.services.ForecastRetrofitService;
import com.example.weatherapp.services.WeatherService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@Module(injects = {
        MainActivityFragment.class,
        WeatherAppApplication.class,
        PreferenceFragment.class,
        ForecastAdapter.class,
        DetailsActivityFragment.class,
        MainActivity.class,
        WeatherService.class,
        WeatherProvider.class})

public class WeatherAppModule {

    private final WeatherAppApplication weatherApp;

    public WeatherAppModule(WeatherAppApplication weatherApp) {
        this.weatherApp = weatherApp;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    ForecastRetrofitService provideForecastService(Retrofit retrofit) {
        return retrofit.create(ForecastRetrofitService.class);
    }

    @Provides
    @Singleton
    WeatherAppSharedPrefs providePreferenceManager() {
        return new WeatherAppSharedPrefs(weatherApp.getApplicationContext());
    }

    @Provides
    @Singleton
    WeatherDBHelper provideWeatherDBHelper() {
        return new WeatherDBHelper(weatherApp.getApplicationContext());
    }
}
