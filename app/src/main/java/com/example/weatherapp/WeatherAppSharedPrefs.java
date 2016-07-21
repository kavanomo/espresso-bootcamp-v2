package com.example.weatherapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class WeatherAppSharedPrefs {
    Context context;

    WeatherAppSharedPrefs(Context context) {
        this.context = context;
    }

    public SharedPreferences getSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getLocationPrefs() {
        return getSharedPrefs().getString(context.getString(R.string.preference_zip_key), context.getString(R.string.preference_zip_default));
    }

    public String getUnitsPrefs() {
        return getSharedPrefs().getString(context.getString(R.string.preference_units_key), context.getString(R.string.units_metric));
    }

    public boolean isMetric() {
        return getUnitsPrefs()
                .equals(context.getString(R.string.units_metric));
    }

    public void setLocationPrefs(String newLocation) {
        getSharedPrefs().edit().putString(context.getString(R.string.preference_zip_key), newLocation).commit();
    }
}
