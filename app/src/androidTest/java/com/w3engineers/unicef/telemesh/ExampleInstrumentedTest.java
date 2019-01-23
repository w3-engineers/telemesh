package com.w3engineers.unicef.telemesh;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.ui.chooseprofileimage.ProfileImageActivity;
import com.w3engineers.unicef.telemesh.ui.splashscreen.SplashActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Rule
    public ActivityTestRule<ProfileImageActivity> mProfileImageTestRule = new ActivityTestRule<>(ProfileImageActivity.class);

    @Test
    public void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getTargetContext();
//
//        assertEquals("com.w3engineers.unicef.telemesh", appContext.getPackageName());

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();

        mProfileImageTestRule.launchActivity(intent);
    }
}
