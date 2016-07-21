package com.example.weatherapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.weatherapp.R;
import com.example.weatherapp.fragments.DetailsActivityFragment;
import com.example.weatherapp.fragments.PreferenceFragment;

public class DetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            DetailsActivityFragment fragment = DetailsActivityFragment.newInstance(getIntent().getData().toString());
            getSupportFragmentManager().beginTransaction().add(R.id.weather_details_fragment, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.weather_details_fragment, new PreferenceFragment())
                        .addToBackStack("settings")
                        .commit();
                return true;
        }
        return false;
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentActivityIntent = super.getSupportParentActivityIntent();
        if (parentActivityIntent != null) {
            return parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return super.getSupportParentActivityIntent();
    }
}
