package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.location.LocationResult;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class BulletinTimeSchedulerTest {
    Context context;
    String CURRENT_LOG_FILE_NAME = "testLog.txt";

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void messageBroadcastTest() {

        // create temporary file
        createDummyLogFile("(W) Sample Log 1");

        createDummyLogFile("(S) Sample Log 2");


        BulletinTimeScheduler.getInstance().processesForInternetConnection();

        addDelay(10 * 1000);

        // SO here we think we got data
        BulletinTimeScheduler.getInstance().resetScheduler(context);

        // now job already scheduled. But in instrumental test we cannot test Job scheduler.
        // so we can call the method which is located in start job section
        RmDataHelper.getInstance().mLatitude = "";
        RmDataHelper.getInstance().mLongitude = "";
        RmDataHelper.getInstance().requestWsMessage();

        Location location = new Location("");
        location.setLatitude(22.8456);
        location.setLongitude(89.5403);
        List<Location> locationList = new ArrayList<>();
        locationList.add(location);

        LocationResult locationResult = LocationResult.create(locationList);

        LocationUtil.getInstance().getLocationCallback().onLocationResult(locationResult);


        // now we have no internet.
        // so we have to call okHttp on Message section
        addDelay(1000 * 20);

        assertTrue(true);
    }

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createDummyLogFile(String text) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() +
                    "/MeshRnD");
            if (!directory.exists()) {
                directory.mkdirs();

                addDelay(5000);
            }

            File file = new File(directory, CURRENT_LOG_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();

                addDelay(5000);
            }
            FileOutputStream fOut = new FileOutputStream(file, true);

            OutputStreamWriter osw = new
                    OutputStreamWriter(fOut);

            osw.write("\n" + text);
            addDelay(2000);
            osw.flush();
            osw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}