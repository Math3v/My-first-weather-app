package com.example.matej.myfirstweatherapp;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by matej on 31.5.2015.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
