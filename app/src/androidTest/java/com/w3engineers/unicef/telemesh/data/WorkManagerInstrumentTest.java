package com.w3engineers.unicef.telemesh.data;


import android.content.Context;

import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import androidx.work.Configuration;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.TestDriver;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.w3engineers.unicef.telemesh.data.analytics.workmanager.NewUserCountWorker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(AndroidJUnit4.class)
public class WorkManagerInstrumentTest {

    // on setup method we will initialize WorkManager in test mode
    // so we can test our worker


    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getTargetContext();
        Configuration config = new Configuration.Builder()
                // Set log level to Log.DEBUG to
                // make it easier to see why tests failed
                .setMinimumLoggingLevel(Log.DEBUG)
                // Use a SynchronousExecutor to make it easier to write tests,
                // without having to deal with multiple threads, locks or latches.
                .setExecutor(new SynchronousExecutor())
                .build();

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(
                context, config);

    }

    // WorkManager has been initialized in test mode, you are ready to test your Workers.


    @Test
    public void testPeriodicWorkNewUserCount() throws Exception{

        // Create request
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(NewUserCountWorker.class, 15, MINUTES)
                        .build();

        WorkManager workManager = WorkManager.getInstance();
        TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver();
        // Enqueue
        workManager.enqueue(request).getResult().get();
        // Tells the testing framework the period delay is met
        testDriver.setPeriodDelayMet(request.getId());
        // Get WorkInfo and outputData
        WorkInfo workInfo = workManager.getWorkInfoById(request.getId()).get();



        // Assert
        assertThat(workInfo.getState(), is(WorkInfo.State.ENQUEUED)); // Since it's a periodic task so initially it get enqueued.


    }
}
