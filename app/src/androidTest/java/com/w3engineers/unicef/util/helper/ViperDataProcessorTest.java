package com.w3engineers.unicef.util.helper;

import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.util.helper.model.ViperData;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class ViperDataProcessorTest {
    private ViperDataProcessor SUT;
    private int type = 1;
    private String data = "Dummy data";

    @Before
    public void setup() {
        SUT = ViperDataProcessor.getInstance();
    }

    @Test
    public void viperDataProcessTest() {
        addDelay(500);
        ViperData res = SUT.setDataFormatFromJson(createDummyJson().getBytes());

        assertEquals(res.dataType,(byte)type);
    }

    private String createDummyJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("t", type);
            jsonObject.put("d", data);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
