package com.example.matej.myfirstweatherapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by matej on 30.5.2015.
 */
public class HttpRequestHandler {

    public String getForecast(URL url) throws IOException {
        InputStream is = null;

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            try {
                conn.connect();
            } catch (Exception e) {
                Log.e(MainActivity.APP_TAG, "Error: " + e.getMessage());
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                Log.e(MainActivity.APP_TAG, "Stack: " + sw.toString());
                return "";
            }
            int response = conn.getResponseCode();
            Log.d(MainActivity.APP_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String jsonForecast = getStringFromInputStream(is);
            Log.d(MainActivity.APP_TAG, jsonForecast);
            return jsonForecast;

        } finally {
            if (is != null)
                is.close();
        }
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}
