package com.example.matej.myfirstweatherapp;

import android.app.Activity;
import android.app.AlertDialog;

import android.util.Log;

/**
 * Created by matej on 30.5.2015.
 */
public class ErrorHandler {

    public static void handle(String subject, String message, Activity activity) {
        Log.e(MainActivity.APP_TAG, message);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(subject);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
