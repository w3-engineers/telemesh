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
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;

    public String getLatitude() {
        return latitude;
    }

    public GeoLocation setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public GeoLocation setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

}