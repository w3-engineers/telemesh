package com.w3engineers.unicef.util.helper;

import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.functions.BiConsumer;

@RunWith(AndroidJUnit4.class)
public class ConnectivityUtilTest {

    private Context mContext;
    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testInternetException(){
        addDelay(200);
        ConnectivityUtil.isInternetAvailable(mContext,null);
        assertTrue(true);
    }

    @Test
    public void testConsumer(){
        BiConsumer<String, Boolean> biConsumer = (key, value) ->
                System.out.println("Key:"+ key+" Value:"+ value);
        addDelay(500);
        ConnectivityUtil.handleException(biConsumer);
        assertTrue(true);
    }

    @Test
    public void testConsumerNull(){
        addDelay(300);
        ConnectivityUtil.handleException(null);
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
