package com.example.matej.myfirstweatherapp;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String APP_TAG = "MyFirstWeatherApp";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    protected TextView tv_temperature;
    protected TextView tv_humidity;
    protected EditText et_town;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        tv_temperature = (TextView) findViewById(R.id.temperature_view);
        tv_humidity = (TextView) findViewById(R.id.humidity_view);
        et_town = (EditText) findViewById(R.id.edit_message);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            //startActivityForResult(i, 1);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Refresh button */
    public void getForecast(View view) {
        String lat, lon, town;
        lat = lon = town = null;

        if(et_town.getText().toString().length() > 0) {
            town = et_town.getText().toString();
        }
        else if(mLocation != null) {
            DecimalFormat df = new DecimalFormat("##.####");
            lat = String.valueOf(df.format(mLocation.getLatitude()));
            lon = String.valueOf(df.format(mLocation.getLongitude()));
            Log.d(APP_TAG, "Latitude: " + lat + " Longitude: " + lon);
        } else {
            ErrorHandler.handle(APP_TAG, "Cannot get latitude and longitude", this);
            return;
        }

        OpenWeatherForecast owf = new OpenWeatherForecast(lat, lon, town, this);
        owf.execute();
    }

    /** Build Google API Client */
    protected synchronized void buildGoogleApiClient() {
        Log.d(APP_TAG, "Inside buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(APP_TAG, "Google Api connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(APP_TAG, "Google Api connection failed");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Log.e(APP_TAG, "Error: " + result.getErrorCode());
            mResolvingError = true;
        }
    }
}
