package com.w3engineers.unicef.telemesh.data.local.meshlog;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MeshLogDataSourceTest {

    MeshLogDataSource SUT;
    private AppDatabase appDatabase;
    private Context mContext;

    @Before
    public void setup() {
        mContext = InstrumentationRegistry.getContext();

        appDatabase = Room.inMemoryDatabaseBuilder(mContext,
                AppDatabase.class).allowMainThreadQueries().build();
        SUT = MeshLogDataSource.getInstance();
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
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}