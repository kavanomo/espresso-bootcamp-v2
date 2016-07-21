package com.example.weatherapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.weatherapp.R;
import com.example.weatherapp.WeatherAppApplication;
import com.example.weatherapp.WeatherAppSharedPrefs;
import com.example.weatherapp.activities.BaseActivity;
import com.example.weatherapp.adapters.ForecastAdapter;
import com.example.weatherapp.data.WeatherContract;
import com.example.weatherapp.services.WeatherService;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;

    @Inject
    WeatherAppSharedPrefs sharedPrefs;

    @Bind(R.id.recyclerview_forecast)
    RecyclerView recyclerView;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    ForecastAdapter adapter;
    String currentLocation = "";
    Parcelable recyclerViewState;
    BroadcastReceiver broadcastReceiver;

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        registerReceiver();
    }

    private void registerReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras().containsKey(WeatherService.SERVICE_ERROR_RESULT)) {
                    String exceptionMessage = intent.getStringExtra(WeatherService.SERVICE_ERROR_RESULT);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), exceptionMessage, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    refresh();
                                }
                            }).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter(WeatherService.SERVICE_RESULT));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WeatherAppApplication.getInstance().inject(this);
        int lastPosition = savedInstanceState == null ? -1 : savedInstanceState.getInt("selectedItem");
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new ForecastAdapter(null, (ForecastAdapter.ItemClickCallback) getActivity(), lastPosition);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());

        ButterKnife.bind(this, root);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItem", adapter.getSelectedItem());
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar toolBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (toolBar != null) {
            toolBar.setTitle("WeatherApp");
        }

        if (recyclerViewState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerViewState = ((LinearLayoutManager) recyclerView.getLayoutManager()).onSaveInstanceState();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                openLocationInMap();
        }
        return false;
    }

    public void onLocationChanged() {
        refresh();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void openLocationInMap() {
        Uri locationUri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", currentLocation)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(locationUri);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        String locationSetting = sharedPrefs.getLocationPrefs();
        Uri weatherForLocationUri = WeatherContract.buildWeatherLocation(
                locationSetting);

        return new CursorLoader(getContext(), weatherForLocationUri,
                null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
        int cityIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        int countryIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COUNTRY_NAME);
        if (cursor.moveToFirst()) {
            currentLocation = cursor.getString(cityIndex) + "," + cursor.getString(countryIndex);
            Snackbar.make(getActivity().findViewById(android.R.id.content), currentLocation, Snackbar.LENGTH_LONG).show();
        } else {
            refresh();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }

    private void refresh() {
        Intent intent = new Intent(getContext(), WeatherService.class);
        intent.putExtra(WeatherService.LOCATION_SETTING_EXTRA, sharedPrefs.getLocationPrefs());
        getContext().startService(intent);
    }
}

