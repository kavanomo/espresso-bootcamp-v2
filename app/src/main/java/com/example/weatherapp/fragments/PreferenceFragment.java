package com.example.weatherapp.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;

import com.example.weatherapp.R;
import com.example.weatherapp.WeatherAppApplication;
import com.example.weatherapp.WeatherAppSharedPrefs;

import javax.inject.Inject;

public class PreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    @Inject
    WeatherAppSharedPrefs sharedPrefs;

    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        WeatherAppApplication.getInstance().inject(this);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (actionBar != null) {
            actionBar.setIcon(new ColorDrawable(ContextCompat.getColor(getContext(), android.R.color.transparent)));
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.action_settings);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.fragment_preference);
        bindPreferenceToValue(findPreference(getString(R.string.preference_zip_key)));
        bindPreferenceToValue(findPreference(getString(R.string.preference_units_key)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
            return true;
        } else if (preference instanceof EditTextPreference) {
            ((EditTextPreference) preference).setText(stringValue);
            sharedPrefs.setLocationPrefs(stringValue);
        }
        preference.setSummary(stringValue);
        return true;
    }

    private void bindPreferenceToValue(Preference preference) {
        String defaultZip = getContext().getString(R.string.preference_zip_default);
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, sharedPrefs.getSharedPrefs().getString(preference.getKey(), defaultZip));
    }
}
