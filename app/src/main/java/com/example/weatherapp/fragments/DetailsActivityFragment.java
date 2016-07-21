package com.example.weatherapp.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.WeatherAppApplication;
import com.example.weatherapp.WeatherAppSharedPrefs;
import com.example.weatherapp.activities.BaseActivity;
import com.example.weatherapp.data.WeatherContract;
import com.example.weatherapp.utils.WeatherUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;

    private static final String[] PROJECTION_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    @Bind(R.id.detail_day_textview)
    TextView detail_day;

    @Bind(R.id.detail_date_textview)
    TextView detail_date;

    @Bind(R.id.detail_high_textview)
    TextView detail_high;

    @Bind(R.id.detail_low_textview)
    TextView detail_low;

    @Bind(R.id.detail_humidity_textview)
    TextView detail_humidity;

    @Bind(R.id.detail_wind_textview)
    TextView detail_wind;

    @Bind(R.id.detail_pressure_textview)
    TextView detail_pressure;

    @Bind(R.id.detail_weather_description_textview)
    TextView detail_weather_description;

    @Bind(R.id.detail_icon)
    ImageView detail_icon;

    @Inject
    WeatherAppSharedPrefs sharedPrefs;

    ShareActionProvider shareActionProvider;
    String weatherForecast;
    Uri weatherUri;

    public static DetailsActivityFragment newInstance(String extras) {
        DetailsActivityFragment f = new DetailsActivityFragment();

        Bundle args = new Bundle();
        args.putString("extras", extras);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WeatherAppApplication.getInstance().inject(this);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, root);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_fragment, menu);

        MenuItem shareButton = menu.findItem(R.id.menu_item_share);
        shareActionProvider = new ShareActionProvider(getContext());
        shareActionProvider.setShareIntent(createShareIntent());
        MenuItemCompat.setActionProvider(shareButton, shareActionProvider);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar toolBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (toolBar != null) {
            Long date = WeatherContract.getDateFromUri(weatherUri);
            String title = WeatherUtils.getDayName(getContext(), date);
            toolBar.setTitle(title);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, weatherForecast);
        return shareIntent;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String uriString = getArguments().getString("extras");
        if (uriString == null) {
            return null;
        }
        weatherUri = Uri.parse(uriString);

        CursorLoader cursorLoader = new CursorLoader(getContext(), weatherUri, PROJECTION_COLUMNS, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            boolean isMetric = sharedPrefs.isMetric();
            String day = WeatherUtils.getDayName(getContext(), cursor.getLong(COL_WEATHER_DATE));
            String dateString = WeatherUtils.formatDate(cursor.getLong(COL_WEATHER_DATE));
            String weatherDescription = cursor.getString(COL_WEATHER_DESC);
            String high = WeatherUtils.formatTemperature(getContext(), cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = WeatherUtils.formatTemperature(getContext(), cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
            String humidity = getContext().getString(R.string.format_humidity, cursor.getFloat(COL_WEATHER_HUMIDITY));
            String wind = WeatherUtils.getFormattedWind(getContext(), isMetric, cursor.getFloat(COL_WEATHER_WIND_SPEED), cursor.getFloat(COL_WEATHER_DEGREES));
            String pressure = getContext().getString(R.string.format_pressure, cursor.getFloat(COL_WEATHER_PRESSURE));
            Drawable icon = ContextCompat.getDrawable(getContext(), WeatherUtils.getArtResourceForWeatherCondition(cursor.getInt(COL_WEATHER_CONDITION_ID)));

            detail_day.setText(day);
            detail_date.setText(dateString);
            detail_high.setText(high);
            detail_low.setText(low);
            detail_humidity.setText(humidity);
            detail_wind.setText(wind);
            detail_pressure.setText(pressure);
            detail_weather_description.setText(weatherDescription);
            detail_icon.setImageDrawable(icon);
            weatherForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(createShareIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    public void onLocationChanged() {
        if (weatherUri == null) {
            return;
        }

        Long date = WeatherContract.getDateFromUri(weatherUri);
        weatherUri = WeatherContract.buildWeatherLocationWithDate(sharedPrefs.getLocationPrefs(), date);
        getArguments().putString("extras", weatherUri.toString());
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
}
