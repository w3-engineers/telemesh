package com.w3.offlinelocationtrack;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.w3.offlinelocationtrack.listener.LocationUpdateListener;

/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/3/2019 at 10:47 AM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose: This module is used to get offline location.
 *  * The location will be updated through GPS.
 *  * If internet connection available the location will be found through Google lcoation provider.
 *  *
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/3/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public class OfflineLocationTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = OfflineLocationTracker.class.getName();
    // Variable list
    private GoogleApiClient googleApiClient;
    private Activity mContext;
    private Criteria criteria;
    private LocationManager locationManager;
    private static String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
    private int BEST_PROVIDER_CODE = 507;
    private LocationUpdateListener listener;

    private long updateTime = 1000L;// Default 1 second time
    private long minimumDistance = 0; // Default 1 meter

    private boolean isNeedToCall;

    public static OfflineLocationTracker getInstance() {
        return LazyHolder.sInsatnce;
    }


    private static class LazyHolder {
        private static final OfflineLocationTracker sInsatnce = new OfflineLocationTracker();
    }

    public OfflineLocationTracker init(Activity context) {
        this.mContext = context;
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        criteria = new Criteria();
        criteria.setSpeedRequired(false);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setAltitudeRequired(false);


        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        boolean isEnable = locationManager.isProviderEnabled(GPS_PROVIDER);

        if (!isEnable) {
            buildAlertMessageNoGps();
            isNeedToCall = false;
        } else {
            isNeedToCall = true;
        }

        return this;
    }

    public OfflineLocationTracker setUpdateTime(long millisecond) {
        this.updateTime = millisecond;
        return this;
    }

    public OfflineLocationTracker setMinimumDistance(int meter) {
        this.minimumDistance = meter;
        return this;
    }

    public void removeLocationUpdate() {
        googleApiClient.disconnect();
        locationManager.removeUpdates(this);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == BEST_PROVIDER_CODE) {
            if (!locationManager.isProviderEnabled(GPS_PROVIDER)) {
                isNeedToCall = false;
                // I am not sure to call always dialog box
            } else {
                isNeedToCall = true;
                requestLocation();
            }
        }
    }

    // User setup method

    public void requestLocation() {
        if (!isNeedToCall) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(GPS_PROVIDER, updateTime, minimumDistance, this);

        googleApiClient.connect();
    }

    public void getLocationListener(LocationUpdateListener listener) {
        this.listener = listener;
    }

    // private method element

    private void initGmsLocation() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(updateTime);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(minimumDistance);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Response: lat= " + location.getLatitude() + " lang= " + location.getLongitude() + " Provider: " + location.getProvider());
                if (listener != null && location != null) {
                    listener.onGetLocation(location);
                }
            }
        });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent mainIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivityForResult(mainIntent, BEST_PROVIDER_CODE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    // Google GMS provider callback

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Connected
        Log.i(TAG, "GMS Connected");
        initGmsLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Local callback

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Response: lat= " + location.getLatitude() + " lang= " + location.getLongitude() + " Provider: " + location.getProvider());
        if (listener != null && location != null) {
            listener.onGetLocation(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
