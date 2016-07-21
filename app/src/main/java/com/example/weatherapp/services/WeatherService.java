package com.example.weatherapp.services;

import android.app.IntentService;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import com.example.weatherapp.WeatherAppApplication;
import com.example.weatherapp.data.WeatherContract;
import com.example.weatherapp.models.City;
import com.example.weatherapp.models.Forecast;
import com.example.weatherapp.models.ForecastList;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.Response;

public class WeatherService extends IntentService {

    public static final String LOCATION_SETTING_EXTRA = "location_setting_extra";
    public static final String SERVICE_RESULT = "service_result";
    public static final String SERVICE_ERROR_RESULT = "service_error_result";
    public static final String SERVICE_SUCCESS_RESULT = "service_success_result";

    private LocalBroadcastManager broadcaster;

    @Inject
    ForecastRetrofitService forecastRetrofitService;

    public WeatherService() {
        super("WeatherService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        WeatherAppApplication.getInstance().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String locationSetting = intent.getStringExtra(LOCATION_SETTING_EXTRA);
        try {
            refresh(locationSetting);
        } catch (IOException e) {
            handleError("Couldn't refresh");
        }
    }

    private void refresh(String locationSetting) throws IOException {
        Response<Forecast> forecastResponse = forecastRetrofitService.getForecast(locationSetting).execute();

        if (!forecastResponse.isSuccess()) {
            handleError(forecastResponse.errorBody().string());
            return;
        }

        Forecast forecast = forecastResponse.body();

        if (!forecast.getCod().equals("200")) {
            handleError(forecast.getMessage());
            return;
        }
        City location = forecast.getCity();
        Long locationId = addLocationToTheDatabase(location.getName(), location.getCountry(), locationSetting, location.getCoordinates().getLatitude(), location.getCoordinates().getLongitude());
        ArrayList<ForecastList> forecastList = forecast.getForecastList();
        addWeatherIntoTheDatabase(locationId, forecastList);
        handleSuccess();
    }

    private void addWeatherIntoTheDatabase(long locationId, ArrayList<ForecastList> forecastList) {
        Uri uri = WeatherContract.WeatherEntry.CONTENT_URI;
        String selection = WeatherContract.WeatherEntry.COLUMN_LOC_KEY + "=?";
        String[] selectionArgs = new String[]{String.valueOf(locationId)};

        Cursor weatherCursor = this.getContentResolver()
                .query(uri, null, selection, selectionArgs, null);

        if (weatherCursor.moveToFirst()) {
            getContentResolver().delete(uri, selection, selectionArgs);
        }

        weatherCursor.close();

        ArrayList<ContentValues> forecastValues = new ArrayList<>();
        for (ForecastList dailyForecast : forecastList) {
            ContentValues forecastValue = new ContentValues();
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_DATE, dailyForecast.getDateTime());
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, dailyForecast.getHumidity());
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, dailyForecast.getPressure());
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, dailyForecast.getSpeed());
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, dailyForecast.getDeg());
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, dailyForecast.getTemp().getMax());
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, dailyForecast.getTemp().getMin());
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, dailyForecast.getWeather().get(0).getMain());
            forecastValue.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, dailyForecast.getWeather().get(0).getId());
            forecastValues.add(forecastValue);
        }
        getContentResolver()
                .bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, forecastValues.toArray(new ContentValues[forecastValues.size()]));
    }

    private Long addLocationToTheDatabase(String city, String country, String locationSetting, double lat, double lon) {
        Long locationId;
        Uri uri = WeatherContract.LocationEntry.CONTENT_URI;
        String[] projection = new String[]{WeatherContract.LocationEntry.TABLE_NAME + "." + WeatherContract.LocationEntry._ID};
        String selection = WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + "=?";
        String[] selectionArgs = new String[]{locationSetting};
        Cursor locationCursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);

        if (locationCursor.moveToFirst()) {
            locationId = locationCursor.getLong(locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID));
        } else {
            ContentValues locationValues = new ContentValues();
            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, city);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COUNTRY_NAME, country);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);
            Uri insertedUri = getContentResolver().insert(WeatherContract.LocationEntry.CONTENT_URI, locationValues);
            locationId = ContentUris.parseId(insertedUri);
        }
        locationCursor.close();
        return locationId;
    }

    private void handleSuccess() {
        Intent intent = new Intent(SERVICE_RESULT).putExtra(SERVICE_SUCCESS_RESULT, "Yay");
        broadcaster.sendBroadcast(intent);
    }

    private void handleError(String error) {
        Intent intent = new Intent(SERVICE_RESULT).putExtra(SERVICE_ERROR_RESULT, error);
        broadcaster.sendBroadcast(intent);
    }
}
