package com.w3engineers.unicef.telemesh.ui.settings;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


@RunWith(AndroidJUnit4.class)
public class SettingsViewModelTest {

    private Context context;
    private SettingsViewModel SUT;

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        SUT = new SettingsViewModel(rule.getActivity().getApplication());
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetLocale_success_setValidData() {

        addDelay(1500);

        String[] languageList = context.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = context.getResources().getStringArray(R.array.language_code_list);

        int dataIndex = 0;

        SUT.setLocale(languageCodeList[dataIndex], languageList[dataIndex]);

        assertEquals(languageList[dataIndex], SUT.getAppLanguage());

        addDelay(700);
    }

    /*@Test
    public void testSetLocale_notSuccess_setAsyncData() {
        addDelay(1500);


        String[] languageList = context.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = context.getResources().getStringArray(R.array.language_code_list);

        int dataIndex = 0;

        SUT.setLocale(languageCodeList[dataIndex], languageList[1]);

        assertNotEquals(languageList[dataIndex], SUT.getAppLanguage());

        addDelay(700);

    }

    @Test
    public void testSetLocale_success_setEmptyData() {

        addDelay(1500);

        String[] languageList = context.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = context.getResources().getStringArray(R.array.language_code_list);

        int dataIndex = 0;

        SUT.setLocale(languageCodeList[dataIndex], "");

        assertEquals(languageList[dataIndex], SUT.getAppLanguage());

        addDelay(700);
    }

    @Test
    public void testSetLocale_success_setNullData() {

        addDelay(3800);

        String[] languageList = context.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = context.getResources().getStringArray(R.array.language_code_list);

        int dataIndex = 0;

        SUT.setLocale(languageCodeList[dataIndex], null);

        assertEquals(languageList[dataIndex], SUT.getAppLanguage());

        addDelay(700);
    }*/

    @Test
    public void testOnCheckedChanged_success_setBoolean() {
        boolean checkedStatus = true;

        SUT.successShared();

        addDelay(1500);

        SUT.onCheckedChanged(checkedStatus);

        assertTrue(SUT.getCheckedStatus());

        addDelay(700);
    }

    @Test
    public void testAppShareMeshOnAndOff_success_setOkay() {
        SUT.closeRmService();

        addDelay(2000);

        SUT.closeInAppShare();

        addDelay(5300);
    }

    /*@Test
    public void testOnCheckedChanged_notSuccess_setBoolean() {
        boolean checkedStatus = false;

        addDelay(1500);

        SUT.onCheckedChanged(checkedStatus);

        assertFalse(SUT.getCheckedStatus());

        addDelay(700);
    }

    @Test
    public void testOnAppShareCount() {
        boolean checkedStatus = false;
        addDelay(1500);
        SUT.successShared();

        addDelay(1500);

        SUT.onCheckedChanged(checkedStatus);

        assertFalse(SUT.getCheckedStatus());

        addDelay(700);
    }*/

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
