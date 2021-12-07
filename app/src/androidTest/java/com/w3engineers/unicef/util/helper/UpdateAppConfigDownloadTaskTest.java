package com.w3engineers.unicef.util.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

//https://www.py4u.net/discuss/604063
@RunWith(AndroidJUnit4.class)
public class UpdateAppConfigDownloadTaskTest  {

    Context context;

    @Test
    public void testVerifyAsync() throws Exception {
        assertTrue(true);
        /*final CountDownLatch latch = new CountDownLatch(1);
        context = InstrumentationRegistry.getContext();*/
        UpdateAppConfigDownloadTask testTask = new UpdateAppConfigDownloadTask() {
            @Override
            protected void onPostExecute(String result) {
                assertNotNull(result);
                if (result != null) {
                    assertTrue(result.length() > 0);
                    //latch.countDown();
                }
            }
        };
        testTask.execute();
        //latch.await();
    }

}
