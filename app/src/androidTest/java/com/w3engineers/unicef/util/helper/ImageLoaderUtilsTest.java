package com.w3engineers.unicef.util.helper;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ImageLoaderUtilsTest {

    Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void contextWrapperTest() {

        ContextWrapper cw = new ContextWrapper(context);
        ImageLoaderUtils.assertValidRequest(cw.getBaseContext());
        assertTrue(true);
    }


}
