package com.example.weatherapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.weatherapp.R;
import com.example.weatherapp.WeatherAppApplication;
import com.example.weatherapp.WeatherAppSharedPrefs;
import com.example.weatherapp.adapters.ForecastAdapter;
import com.example.weatherapp.fragments.DetailsActivityFragment;
import com.example.weatherapp.fragments.MainActivityFragment;
import com.example.weatherapp.fragments.PreferenceFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements ForecastAdapter.ItemClickCallback {

    private static final String FORECAST_FRAGMENT_TAG = "forecast_fragment";
    private static final String DETAILS_FRAGMENT_TAG = "details_fragment";

    @Inject
    WeatherAppSharedPrefs sharedPrefs;

    @Nullable
    @Bind(R.id.weather_details_fragment)
    FrameLayout weatherDetailsFragment;

    public static Boolean isTabletLayout;

    String currentLocation;
    MainActivityFragment mainActivityFragment = MainActivityFragment.newInstance();
    DetailsActivityFragment detailsFragment = DetailsActivityFragment.newInstance(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeatherAppApplication.getInstance().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        isTabletLayout = weatherDetailsFragment != null;
        currentLocation = sharedPrefs.getLocationPrefs();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.forecast_fragment, mainActivityFragment, FORECAST_FRAGMENT_TAG)
                    .commit();

            if (isTabletLayout) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.weather_details_fragment, detailsFragment, DETAILS_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocation();
    }

    @Override
    public void onBackStackChanged() {
        super.onBackStackChanged();
        updateLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.forecast_fragment, new PreferenceFragment())
                        .addToBackStack("settings")
                        .commit();
                return true;
        }
        return false;
    }

    private void updateLocation() {
        if (!sharedPrefs.getLocationPrefs().equals(currentLocation)) {
            MainActivityFragment forecastFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentByTag(FORECAST_FRAGMENT_TAG);
            forecastFragment.onLocationChanged();
            DetailsActivityFragment detailsFragment = (DetailsActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILS_FRAGMENT_TAG);
            if (isTabletLayout && detailsFragment != null) {
                detailsFragment.onLocationChanged();
            }
            currentLocation = sharedPrefs.getLocationPrefs();
        }
    }

    @Override
    public void onItemSelected(Uri uri) {
        if (!isTabletLayout) {
            Intent detailsActivityIntent = new Intent(this, DetailsActivity.class);
            detailsActivityIntent.setData(uri);
            startActivity(detailsActivityIntent);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.weather_details_fragment, DetailsActivityFragment.newInstance(uri.toString()), DETAILS_FRAGMENT_TAG)
                    .commit();
        }
    }
}
