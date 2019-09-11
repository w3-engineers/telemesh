package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.LocalBroadcastManager;

import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.WebSocketListener;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class BulletinTimeSchedulerTest {
    Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void messageBroadcastTest() {
        BulletinTimeScheduler.NetworkCheckReceiver receiver = BulletinTimeScheduler.getInstance().getReceiver();

        // fake calling in Broadcast
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(Intent.ACTION_PACKAGE_REPLACED));

        Intent intent = new Intent(Intent.ACTION_PACKAGE_REPLACED);
        intent.setAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        addDelay(2000);

        // SO here we think we got data
        BulletinTimeScheduler.getInstance().resetScheduler(context);

        // now job already scheduled. But in instrumental test we cannot test Job scheduler.
        // so we can call the method which is located in start job section
        RmDataHelper.getInstance().requestWsMessage();


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
}