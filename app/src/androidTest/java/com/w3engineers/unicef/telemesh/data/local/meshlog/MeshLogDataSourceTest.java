package com.w3engineers.unicef.telemesh.data.local.meshlog;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import android.content.Context;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedDataSource;
import com.w3engineers.unicef.telemesh.data.local.feed.FeedEntity;
import com.w3engineers.unicef.telemesh.data.local.feedback.FeedbackDataSource;
import com.w3engineers.unicef.util.helper.StatusHelper;

import org.junit.After;
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
public class MeshLogDataSourceTest {

    MeshLogDataSource SUT;
    private AppDatabase appDatabase;
    private Context mContext;

    private FeedbackDataSource feedbackDataSource;
    private FeedDataSource feedDataSource;

    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getContext();

        appDatabase = Room.inMemoryDatabaseBuilder(mContext,
                AppDatabase.class).allowMainThreadQueries().build();
        SUT = MeshLogDataSource.getInstance();
        feedbackDataSource = FeedbackDataSource.getInstance();
        feedDataSource  = FeedDataSource.getInstance();
    }

    @After
    public void tearDown() {
        appDatabase.close();
    }

    @Test
    public void dataInsertionErrorTest() {
        addDelay(500);

        long res = SUT.insertOrUpdateData(null);

        addDelay(1000);

        assertEquals(0, res);

        addDelay(500);

        res = feedbackDataSource.insertOrUpdate(null);

        addDelay(1000);

        assertEquals(0, res);

        addDelay(500);

        FeedEntity r = feedDataSource.insertOrUpdateData(null);

        addDelay(1000);

        assertEquals(null, r);

        addDelay(500);

        res = feedDataSource.updateFeedMessageReadStatus(null);

        addDelay(1000);

        assertEquals(0, res);

        addDelay(500);

        StatusHelper.out("Test executed");
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}