package com.w3engineers.unicef.util.helper.uiutil;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.widget.ImageView;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class UIHelperTest {
    private Context mContext;
    private UIHelper SUT;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        SUT = new UIHelper();
    }

    /**
     * This test cover different message status icon set in
     * Glide with different case.
     */
    @Test
    public void setImageStatusDeliveredResourceTest() {
        addDelay(500);

        int status = Constants.MessageStatus.STATUS_DELIVERED;
        ImageView imageView = new ImageView(mContext); // test image view
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                UIHelper.setImageStatusResource(imageView, status);

                addDelay(2000);

                assertNull(imageView.getDrawable());
            }
        }, 1);

    }

    @Test
    public void setImageStatusFailedResourceTest() {
        addDelay(500);

        int status = 0;
        ImageView imageView = new ImageView(mContext); // test image view
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                UIHelper.setImageStatusResource(imageView, status);

                addDelay(2000);

                assertNull(imageView.getDrawable());
            }
        }, 1);

    }

    @Test
    public void dateSeparationTest() {
        addDelay(500);
        // Test for previous date test
        MessageEntity entity = new MessageEntity();

        // create previous date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        entity.time = cal.getTimeInMillis();

        String time = UIHelper.getSeparatorDate(entity);
        assertEquals(time, "Yesterday");
        addDelay(500);

        // test other date thats not today or yesterday
        cal.add(Calendar.DATE, -3);
        entity.time = cal.getTimeInMillis();
        String otherTIme = UIHelper.getSeparatorDate(entity);
        assertNotEquals(otherTIme, "Yesterday");
        addDelay(500);

        // null pointer check
        String nullTime = UIHelper.getSeparatorDate(null);
        assertNull(nullTime);
        addDelay(500);
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

