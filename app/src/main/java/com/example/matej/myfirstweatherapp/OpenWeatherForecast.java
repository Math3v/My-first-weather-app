package com.example.matej.myfirstweatherapp;

import android.util.Log;

import com.google.android.gms.appdatasearch.GetRecentContextCall;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by matej on 29.5.2015.
 */
public class OpenWeatherForecast {

    private URL url;

    public OpenWeatherForecast(String lat, String lon) {
        try {
            this.url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon);
        } catch (MalformedURLException e) {

        }
    }

    public String getForecast() throws IOException {
        InputStream is = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(MainActivity.APP_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return is.toString();

        } finally {
            if (is != null)
                is.close();
        }
    }
}
