package com.w3engineers.unicef.util.helper;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TimeUtilTest {
    private TimeUtil timeUtil;

    @Before
    public void setup(){
        timeUtil = TimeUtil.getInstance();
    }

    @Test
    public void timeUtilExceptionHandleTest(){

        addDelay();

        String fullTimeError = TimeUtil.getBroadcastFullTime("100");

        assertTrue(TextUtils.isEmpty(fullTimeError));

        addDelay();

        String broadcastTimeError = TimeUtil.getBroadcastTime("100");

        assertTrue(TextUtils.isEmpty(broadcastTimeError));

        addDelay();


        Date stringToDateError = TimeUtil.stringToDate("100");

        assertNull(stringToDateError);

        addDelay();

    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}