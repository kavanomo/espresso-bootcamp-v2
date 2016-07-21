package com.example.weatherapp;

import android.app.Application;

import dagger.ObjectGraph;

public class WeatherAppApplication extends Application {
    protected ObjectGraph objectGraph;
    static WeatherAppApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        objectGraph = ObjectGraph.create(getModules());
    }

    protected Object[] getModules() {
        final Object[] modules = new Object[1];
        modules[0] = new WeatherAppModule(this);
        return modules;
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }

    public static WeatherAppApplication getInstance() {
        return application;
    }
}
