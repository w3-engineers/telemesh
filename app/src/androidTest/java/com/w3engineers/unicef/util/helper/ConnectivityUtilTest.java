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
        BiConsumer<String, Boolean> biConsumer = (key, value) ->
                System.out.println("Key:"+ key+" Value:"+ value);
        ConnectivityUtil.isInternetAvailable(mContext,null);
        assertTrue(true);
    }


}
