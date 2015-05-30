package com.example.matej.myfirstweatherapp;

import android.os.AsyncTask;
import android.util.Log;

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
    private MainActivity context;

    private String s_temperature;
    private String s_humidity;

    public OpenWeatherForecast(String lat, String lon, String town, MainActivity context) {

        this.context = context;

        try {
            if(town != null && town.length() > 0) {
                this.url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + town + "&units=metric");
            } else {
                this.url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric");
            }
        } catch (MalformedURLException e) {
            ErrorHandler.handle(MainActivity.APP_TAG, "Malformed URL: " + url, context);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject ojs;
        HttpRequestHandler http = new HttpRequestHandler(context);
        try {
            ojs = new JSONObject(http.getForecast(url));
            String main = ojs.getString("main");

            s_temperature = new JSONObject(main).getString("temp");
            s_humidity =    new JSONObject(main).getString("humidity");

        } catch (IOException e) {
            ErrorHandler.handle(MainActivity.APP_TAG,  e.getMessage(), context);
        } catch (JSONException e) {
            ErrorHandler.handle(MainActivity.APP_TAG, e.getMessage(), context);
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        context.tv_temperature.setText(s_temperature);
        context.tv_humidity.setText(s_humidity);
    }

}
