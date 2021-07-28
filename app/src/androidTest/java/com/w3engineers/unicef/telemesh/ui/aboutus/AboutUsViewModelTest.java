package com.w3engineers.unicef.telemesh.ui.aboutus;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.BuildConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@RunWith(AndroidJUnit4.class)
public class AboutUsViewModelTest {

    private AboutUsViewModel SUT;

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setUp() {
        SUT = new AboutUsViewModel(rule.getActivity().getApplication());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getAppVersion_matched_useValidBuildVersion() {
        addDelay(500);
        String buildVersion = "Version:" + BuildConfig.VERSION_NAME;
        assertEquals(SUT.getAppVersion(), buildVersion);
        addDelay(1000);
    }

    /*@Test
    public void getAppVersion_notMatched_useEmptyBuildVersion() {
        addDelay(500);
        String buildVersion = "Version:" + "";
        assertNotEquals(SUT.getAppVersion(), buildVersion);
        addDelay(1000);
    }

    @Test
    public void getAppVersion_success_useNullBuildVersion() {
        addDelay(500);
        String buildVersion = "Version:" + null;
        assertNotEquals(SUT.getAppVersion(), buildVersion);
        addDelay(1000);
    }*/

    private void addDelay(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}