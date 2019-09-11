package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


public class LocationUtil {

    private LocationManager locationManager;
    private Context mContext;
    private long updateTime = 1000L;// Default 1 second time
    private long minimumDistance = 0; // Default 1 meter
    private LocationRequestCallback callback;

    private FusedLocationProviderClient mFusedLocationClient;

    public static LocationUtil getInstance() {
        return LazyHolder.sInstance;
    }

    private static class LazyHolder {
        private static final LocationUtil sInstance = new LocationUtil();
    }

    public void addLocationListener(LocationRequestCallback callback) {
        this.callback = callback;
    }

    public LocationUtil init(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        return this;
    }

    public LocationUtil getLocation() {

       /* mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("LocationTest", "lastKnown Lat: " + location.getLatitude() + " lang: " + location.getLongitude());
                    if (callback != null) {
                        callback.onGetLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                    }

                } else {
                    requestLocation();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                requestLocation();
            }
        });*/

        requestLocation();

        return this;
    }

    public void removeListener() {
        try {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestLocation() {
        mFusedLocationClient.requestLocationUpdates(getLocationRequest(), locationCallback, Looper.getMainLooper());
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult == null) return;

            for (Location location : locationResult.getLocations()) {
                if (location != null) {
                    Log.d("LocationTest", "Lat: " + location.getLatitude() + " lang: " + location.getLongitude());

                    if (callback != null) {
                        callback.onGetLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        callback = null;
                        removeListener();
                    }
                }
            }
        }
    };

    private LocationRequest getLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(updateTime);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(minimumDistance);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return mLocationRequest;
    }

    public interface LocationRequestCallback {
        void onGetLocation(String lat, String lang);
    }

}
