package com.example.matej.myfirstweatherapp;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String APP_TAG = "MyFirstWeatherApp";

    public static final String KEY_LOCATION = "MyWeatherAppLocation";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Display text in new activity */
    private boolean displayTextInActivity(String text) {
        Intent intent = new Intent(this, GetLocation.class);
        intent.putExtra(KEY_LOCATION, text);
        startActivity(intent);

        return true;
    }

    /** Called when the user clicks the Refresh button */
    public void getLocation(View view) {
        String location;
        if(mLocation != null) {
            DecimalFormat df = new DecimalFormat("#.00");
            String lat = String.valueOf(df.format(mLocation.getLatitude()));
            String lon = String.valueOf(df.format(mLocation.getLongitude()));
            location = lat + "\n" + lon;
        } else {
            location = "Location is null";
        }

        displayTextInActivity(location);
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
        Log.d(APP_TAG, "Google Api connection failed");
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
            Log.d(APP_TAG, "Error: " + result.getErrorCode());
            mResolvingError = true;
        }
    }
}
