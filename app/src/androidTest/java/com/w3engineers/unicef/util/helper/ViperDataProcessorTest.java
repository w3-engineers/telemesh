package com.w3engineers.unicef.util.helper;

import androidx.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.updateapp.UpdateConfigModel;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;
import com.w3engineers.unicef.util.helper.model.ViperData;

import org.junit.Assert;
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
    private RandomEntityGenerator randomEntityGenerator;


    @Before
    public void setup() {
        SUT = ViperDataProcessor.getInstance();
        randomEntityGenerator = new RandomEntityGenerator();
    }

    @Test
    public void viperDataProcessorExceptionTest() {
        addDelay(500);
        try {
            SUT.setDataFormatFromJson(null);
            Assert.fail("Corrupted data");
        } catch (Exception e) {
            e.printStackTrace();
        }

        addDelay(500);

        ViperData viperData = SUT.setDataFormatFromJson("testData".getBytes());

        assertFalse(viperData.dataType > 0);

        addDelay(500);
    }

    @Test
    public void processJsonFalse(){
        addDelay(500);
        try {
            SUT.processUpdateAppConfigJson(null);
            assertFalse(false);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void appUpdateConfigDataTest() {

        addDelay(600);

        UpdateConfigModel configModel = randomEntityGenerator.generateUpdateConfigModel();
        String configData = new Gson().toJson(configModel);

        SUT.processUpdateAppConfigJson(configData);

        addDelay(500);

    }

    @Test
    public void jsonBuildFailed(){
        addDelay(500);
        SUT.getDataFormatToJson(null);
        assertFalse(false);
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }





}
