package com.w3engineers.unicef.telemesh.data.local.feed;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeoLocation {

    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public GeoLocation setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public GeoLocation setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

}