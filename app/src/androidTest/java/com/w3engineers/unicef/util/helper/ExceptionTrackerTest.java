package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class ExceptionTrackerTest {

    Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void exceptionCaughtAndWriteTest() {

        addDelay(500);

        LogProcessUtil.getInstance().writeCrash("Test exception");

        addDelay(10 * 1000);

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                context.getString(R.string.app_name));

        File directory = new File(file, Constants.AppConstant.LOG_FOLDER);

        File crashFile = new File(directory, Constants.AppConstant.CRASH_REPORT_FILE_NAME);

        // for writing exception when file already created

        LogProcessUtil.getInstance().writeCrash("Test exception 3");

        addDelay(5 * 1000);

        assertTrue(crashFile.exists());
    }

    @Test
    public void exceptionCaughtTest() {
        addDelay(500);

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();

                try{
                    throw new RuntimeException("Mew");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };


        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        addDelay(1000);

    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}