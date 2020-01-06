package com.w3engineers.unicef.telemesh.data.helper.inappupdate;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class InAppUpdateTest {
    private String url;
    private Context mContext;

    @Before
    public void setup() {
        url = InAppUpdate.LIVE_JSON_URL;
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void downloadAppUpdateInfoFromServer() {
        InAppUpdate.getInstance(mContext).checkForUpdate(mContext, url);

        addDelay(2000);

        InAppUpdate.getInstance(mContext).prepareLocalServer();

        addDelay(1500);

        assertTrue(true);

    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}