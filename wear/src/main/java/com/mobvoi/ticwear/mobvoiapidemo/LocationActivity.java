package com.mobvoi.ticwear.mobvoiapidemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.location.LocationListener;
import com.mobvoi.android.location.LocationRequest;
import com.mobvoi.android.location.LocationServices;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.wear.app.PermissionCompat;

public class LocationActivity extends Activity implements
        MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener, PermissionCompat.OnRequestPermissionsResultCallback {

    private final static int PERMISSIONS_REQUEST_COARSE = 0;
    private final static int PERMISSIONS_REQUEST_FINE = 1;

    private MobvoiApiClient mMobvoiApiClient;
    private TextView mLocationInfo;
    private Button mGetLastLocation;
    private Button mRequestLocation;
    private boolean mConnected;
    private boolean mRequestStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mLocationInfo = (TextView) findViewById(R.id.location_info);
        mGetLastLocation = (Button) findViewById(R.id.get_last_location);
        mRequestLocation = (Button) findViewById(R.id.request_location);
        mGetLastLocation.setOnClickListener(this);
        mRequestLocation.setOnClickListener(this);

        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMobvoiApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestStart) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mMobvoiApiClient, this);
            mRequestStart = false;
        }
        mMobvoiApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mConnected = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocationInfo(location);
    }

    @Override
    public void onClick(View v) {
        if (!mConnected) {
            mLocationInfo.setText("onConnectionFailed");
            return;
        }
        switch (v.getId()) {
            case R.id.get_last_location:
                if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                        PERMISSIONS_REQUEST_COARSE)) {
                    getLastLocation();
                }
                break;
            case R.id.request_location:
                if (!mRequestStart) {
                    if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,
                            PERMISSIONS_REQUEST_FINE)) {
                        requestLocation();
                    }
                }
                break;
        }
    }

    private void updateLocationInfo(Location location) {
        mLocationInfo.setText("lat: " + location.getLatitude() + ", alt: " + location.getAltitude());
    }

    private void getLastLocation() {
        Location location =
                LocationServices.FusedLocationApi.getLastLocation(mMobvoiApiClient);
        updateLocationInfo(location);
    }

    private void requestLocation() {
        LocationRequest mLocationRequest = new LocationRequest()
                .setInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mMobvoiApiClient, mLocationRequest, this);
        mRequestStart = true;
    }

    public boolean checkPermission(String permission, int code) {
        if (PermissionCompat.checkSelfPermission(this,
                permission) != PackageManager.PERMISSION_GRANTED) {
            if (PermissionCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            } else {
                PermissionCompat.requestPermissions(this,
                        new String[]{permission},
                        code);
            }
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("Override")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_COARSE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case PERMISSIONS_REQUEST_FINE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
