package com.example.matej.myfirstweatherapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by matej on 29.5.2015.
 */
public class OpenWeatherForecast extends AsyncTask <String, Void, String> {

    private URL url;
    private TextView temp;
    private TextView humid;
    private String stemp;
    private String shumid;

    public OpenWeatherForecast(String lat, String lon, TextView temp, TextView humid) {
        this.temp = temp;
        this.humid = humid;
        try {
            this.url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric");
        } catch (MalformedURLException e) {
            Log.e(MainActivity.APP_TAG, "Malformed URL: " + url);
        }
        Log.d(MainActivity.APP_TAG, "URL: " + url);
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject ojs;
        HttpRequestHandler http = new HttpRequestHandler();
        try {
            ojs = new JSONObject(http.getForecast(url));
            String main = ojs.getString("main");

            stemp = new JSONObject(main).getString("temp");
            shumid = new JSONObject(main).getString("humidity");

        } catch (IOException e) {
            Log.e(MainActivity.APP_TAG, e.getMessage());
        } catch (JSONException e) {
            Log.e(MainActivity.APP_TAG, e.getMessage());
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        temp.setText(stemp);
        humid.setText(shumid);
    }

}
