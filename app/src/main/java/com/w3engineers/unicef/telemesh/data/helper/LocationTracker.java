package com.w3engineers.unicef.telemesh.data.helper;

/*
============================================================================
Copyright (C) 2021 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.annotations.NonNull;

public class LocationTracker extends Service {
    private static LocationTracker locationTracker;

    private static Context mContext;
    // private static Activity mActivity;

    private Executor executor;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;  //1000; // 10 meters 5

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; //1000 * 60 * 1; // 1 minute 0

    // Declaring a Location Manager
    protected LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;

    private LocationTracker() {
    }


    public static LocationTracker getInstance(Context context) {
        if (locationTracker == null) {
            synchronized (LocationTracker.class) {
                if(locationTracker == null){
                    locationTracker = new LocationTracker(context);
                }
            }
        }
        return locationTracker;
    }

    public static LocationTracker getInstance() {
        if (locationTracker == null) {
            synchronized (LocationTracker.class) {
                if (locationTracker ==null){
                    locationTracker = new LocationTracker();
                }
            }
        }
        return locationTracker;
    }

    private LocationTracker(Context context) {
        this.mContext = context;
        executor = Executors.newSingleThreadExecutor();
        //   getLocation();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {

            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    int requestPermissionsCode = 50;
            /*        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);
                }*/

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                       /* if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);

                        } else {*/
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {

                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                        //  }
                    }
                }

                if (location == null & mContext != null) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
          /*      if (ContextCompat.checkSelfPermission(mActiivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 50);

                }*/
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        Log.d("LocationDemoActivity", "Success trying to get last GPS location");
                                        // Logic to handle location object
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }

                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("LocationDemoActivity", "Error trying to get last GPS location");
                                    e.printStackTrace();
                                }
                            });
                }

                if (latitude == 0.0 || longitude == 0.0){
                    getLastKnownLocation();
                }
            }




            //Age of location returned from LocationServices
    /*        if (location != null) {
                long locationAge = System.currentTimeMillis() - location.getTime();
                if (locationAge <= 60 * 1000) { // not older than 60 seconds

                }
            }
*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnownLocation() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);

            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Log.d("found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }else {
            latitude = bestLocation.getLatitude();
            latitude = bestLocation.getLongitude();
        }
        return bestLocation;
    }


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
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
    };

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }


    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void stopListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(mLocationListener);
        }
    }
}

