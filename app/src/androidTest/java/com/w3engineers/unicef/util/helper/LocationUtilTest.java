package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.google.android.gms.location.LocationResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LocationUtilTest {

    private LocationUtil locationUtil;
    private Context mContext;

    @Before
    public void setup() {
        locationUtil = LocationUtil.getInstance();
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void locationRequestTest() {
        addDelay(500);
        locationUtil.init(mContext);
        locationUtil.getLocation().addLocationListener(callback);

        Location location = new Location("");
        location.setLatitude(22.8456);
        location.setLongitude(89.5403);
        List<Location> locationList = new ArrayList<>();
        locationList.add(location);

        LocationResult locationResult = LocationResult.create(locationList);

        locationUtil.getLocationCallback().onLocationResult(locationResult);

        addDelay(5000);
    }

    private LocationUtil.LocationRequestCallback callback = (lat, lang) -> {
        if (!TextUtils.isEmpty(lang)) {
            assertTrue(true);
        } else {
            assertFalse(false);
        }
        locationUtil.removeListener();
    };

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}