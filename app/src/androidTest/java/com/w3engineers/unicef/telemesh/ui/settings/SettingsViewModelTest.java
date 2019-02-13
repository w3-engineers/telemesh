package com.w3engineers.unicef.telemesh.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [29-Jan-2019 at 12:51 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [29-Jan-2019 at 12:51 PM].
 * --> <Second Editor> on [29-Jan-2019 at 12:51 PM].
 * Reviewed by :
 * --> <First Reviewer> on [29-Jan-2019 at 12:51 PM].
 * --> <Second Reviewer> on [29-Jan-2019 at 12:51 PM].
 * ============================================================================
 **/
@RunWith(AndroidJUnit4.class)
public class SettingsViewModelTest {

    private SharedPreferences sharedPreferences;
    private Context context;
    SettingsViewModel SUT;

    @Rule
    public ActivityTestRule<MainActivity> rule  = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        SUT = new SettingsViewModel(rule.getActivity().getApplication());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetLocale_success_setValidData() {

        String[] languageList = context.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = context.getResources().getStringArray(R.array.language_code_list);

        int dataIndex = 0;

        SUT.setLocale(languageCodeList[dataIndex], languageList[dataIndex]);

        assertEquals(languageList[dataIndex], SUT.getAppLanguage());
    }

    @Test
    public void testSetLocale_notSuccess_setAsyncData() {

        String[] languageList = context.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = context.getResources().getStringArray(R.array.language_code_list);

        int dataIndex = 0;

        SUT.setLocale(languageCodeList[dataIndex], languageList[1]);

        assertNotEquals(languageList[dataIndex], SUT.getAppLanguage());
    }

    @Test
    public void testSetLocale_success_setEmptyData() {

        String[] languageList = context.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = context.getResources().getStringArray(R.array.language_code_list);

        int dataIndex = 0;

        SUT.setLocale(languageCodeList[dataIndex], "");

        assertEquals(languageList[dataIndex], SUT.getAppLanguage());
    }

    @Test
    public void testSetLocale_success_setNullData() {

        String[] languageList = context.getResources().getStringArray(R.array.language_list);//{"English", "Bangla"};
        String[] languageCodeList = context.getResources().getStringArray(R.array.language_code_list);

        int dataIndex = 0;

        SUT.setLocale(languageCodeList[dataIndex], null);

        assertEquals(languageList[dataIndex], SUT.getAppLanguage());
    }

    @Test
    public void testOnCheckedChanged_success_setBoolean() {
        boolean checkedStatus = true;

        SUT.onCheckedChanged(checkedStatus);

        assertTrue(SUT.getCheckedStatus());
    }

    @Test
    public void testOnCheckedChanged_notSuccess_setBoolean() {
        boolean checkedStatus = false;

        SUT.onCheckedChanged(checkedStatus);

        assertFalse(SUT.getCheckedStatus());
    }
}