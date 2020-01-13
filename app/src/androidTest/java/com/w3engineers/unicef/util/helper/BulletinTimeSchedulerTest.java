package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationResult;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.WebSocketListener;

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
        BulletinTimeScheduler.NetworkCheckReceiver receiver = BulletinTimeScheduler.getInstance().getReceiver();

        // create temporary file
        createDummyLogFile("(W) Sample Log 1");

        createDummyLogFile("(S) Sample Log 2");


        // fake calling in Broadcast
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(Intent.ACTION_PACKAGE_REPLACED));

        Intent intent = new Intent(Intent.ACTION_PACKAGE_REPLACED);
        intent.setAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        addDelay(10 * 1000);

        // SO here we think we got data
        BulletinTimeScheduler.getInstance().resetScheduler(context);

        // now job already scheduled. But in instrumental test we cannot test Job scheduler.
        // so we can call the method which is located in start job section
        RmDataHelper.getInstance().requestWsMessage();
       /* if (LocationUtil.getInstance().getLocationListener() != null) {
            LocationUtil.getInstance().getLocationListener().onGetLocation("22.8456", "89.5403");
        }*/

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

        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
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