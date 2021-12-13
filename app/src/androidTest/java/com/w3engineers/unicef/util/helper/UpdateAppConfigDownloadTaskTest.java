package com.w3engineers.unicef.util.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.w3engineers.unicef.telemesh.data.updateapp.UpdateConfigModel;
import com.w3engineers.unicef.telemesh.util.RandomEntityGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

//https://www.py4u.net/discuss/604063
@RunWith(AndroidJUnit4.class)
public class UpdateAppConfigDownloadTaskTest  {



    @Before
    public void setup() {

    }

    @Test
    public void testVerifyAsync() throws Exception {

        /*UpdateAppConfigDownloadTask testTask = new UpdateAppConfigDownloadTask() {
        };
        testTask.execute();*/

    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
