package com.example.matej.myfirstweatherapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by matej on 29.5.2015.
 */
public class CurrentOpenWeather extends AsyncTask <String, Void, String> {

    private URL url;
    private MainActivity context;

    private String s_temperature;
    private String s_humidity;
    private String s_icon;
    private byte[] b_icon;

    public CurrentOpenWeather(String lat, String lon, String town, MainActivity context) {
        this.context = context;
        buildUrl(lat, lon, town);
    }

    private void buildUrl(String lat, String lon, String town) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String tempUnit = prefs.getString("pref_temperatureUnit", "NULL");
        String s_url = "";

        try {
            if(town != null && town.length() > 0) {
                s_url = "http://api.openweathermap.org/data/2.5/weather?q=" + town;
            } else {
                s_url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon;
            }

            if(tempUnit.equals("celzius")) {
                s_url += "&units=metric";
            }
            else {
                s_url += "&units=imperial";
            }
            this.url = new URL(s_url);

        } catch (MalformedURLException e) {
            ErrorHandler.handle(MainActivity.APP_TAG, "Malformed URL: " + url, context);
        }

        Log.d(context.APP_TAG, "Temperature unit: " + tempUnit);
        Log.d(context.APP_TAG, "URL: " + s_url);
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject ojs;
        HttpRequestHandler http = new HttpRequestHandler(context);
        try {
            ojs = new JSONObject(http.getForecast(url));
            String main = ojs.getString("main");

            s_temperature = new JSONObject(main).getString("temp");                     // Get temperature
            s_humidity =    new JSONObject(main).getString("humidity");                 // Get humidity value
            s_icon =        new JSONObject(                                             // Get weather from root JSON
                            new JSONArray(ojs.getString("weather")).get(0).toString()   // Get first element of weather array
                            ).getString("icon");                                        // Get value of icon

            /* Get weather icon */
            String iconUrl = "http://openweathermap.org/img/w/" + s_icon + ".png";
            Log.d(context.APP_TAG, "IconURL: " + iconUrl);
            b_icon = ImageDownloader.download(iconUrl);

            /* Get sunrise */
            String sunrise = (new JSONObject(ojs.getString("sys"))).getString("sunrise");
            parseDateTime(sunrise);

            /* Get sunset */
            String sunset = (new JSONObject(ojs.getString("sys"))).getString("sunset");
            parseDateTime(sunset);

        } catch (IOException e) {
            ErrorHandler.handle(MainActivity.APP_TAG,  e.getMessage(), context);
        } catch (JSONException e) {
            ErrorHandler.handle(MainActivity.APP_TAG, e.getMessage(), context);
        }

        return "";
    }

    private void parseDateTime(String time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(time));
        /**
         * TODO: Time is set poorly resulting in wrong values
         */

        Log.d(context.APP_TAG,  "Raw: "         + time +
                                " Day: "        + cal.get(Calendar.DAY_OF_MONTH) +
                                " Hours: "      + cal.get(Calendar.HOUR_OF_DAY) +
                                " Minutes: "    + cal.get(Calendar.MINUTE));
    }

    @Override
    protected void onPostExecute(String s) {
        formatTemperature();
        formatHumidity();

        context.tv_temperature.setText(s_temperature);
        context.tv_humidity.setText(s_humidity);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inScaled = false;
        Bitmap raw = BitmapFactory.decodeByteArray(b_icon, 0, b_icon.length, opts);
        Bitmap scaled = Bitmap.createScaledBitmap(raw, raw.getWidth() * 32, raw.getHeight() * 32, true);

        context.iv_icon.setImageBitmap(scaled);
    }

    protected void formatHumidity() {
        s_humidity += " %";
    }

    protected void formatTemperature() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String tempUnit = prefs.getString("pref_temperatureUnit", "NULL");
        DecimalFormat df = new DecimalFormat("##.#");

        try {
            s_temperature = df.format(Double.parseDouble(s_temperature));
        } catch (IllegalArgumentException e) {
            Log.e(context.APP_TAG, "Error: [DecimalFormat] " + e.getMessage());
        }

        if(tempUnit.equals("celzius")) {
            s_temperature += "  \u2103";
        } else {
            s_temperature += " \u2109";
        }
    }

    /**
     * I LOVE YOUUUUUUU <3
     */
}
