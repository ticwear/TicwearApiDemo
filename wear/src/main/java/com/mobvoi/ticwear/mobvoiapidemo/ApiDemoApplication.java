package com.mobvoi.ticwear.mobvoiapidemo;

import android.app.Application;
import android.util.Log;

public class ApiDemoApplication extends Application {
    private static final String TAG = "TicwearApiDemo";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "app start...");
    }
}
