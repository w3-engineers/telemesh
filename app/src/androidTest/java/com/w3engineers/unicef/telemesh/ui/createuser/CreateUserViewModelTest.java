package com.w3engineers.unicef.telemesh.ui.createuser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@RunWith(AndroidJUnit4.class)
public class CreateUserViewModelTest {

    private SharedPreferences sharedPreferences;
    CreateUserViewModel SUT;

    @Rule
    public ActivityTestRule<CreateUserActivity> rule  = new ActivityTestRule<>(CreateUserActivity.class);

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        SUT = new CreateUserViewModel(rule.getActivity().getApplication());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void storeData_storeNameString_getNameString() {
        String firstName = "Daniel";
        String lastName = "Alvez";
        int imageIndex = 1;

        SUT.setImageIndex(imageIndex);
        SUT.storeData(firstName, lastName);

        checkValues(firstName, lastName, imageIndex);
    }

    @Test
    public void storeData_storeNameEmpty_getNameEmpty() {
        String firstName = "";
        String lastName = "";
        int imageIndex = 5;

        SUT.setImageIndex(imageIndex);
        SUT.storeData(firstName, lastName);

        checkValues(firstName, lastName, imageIndex);
    }

    @Test
    public void storeData_storeNameNull_getNameNull() {
        String firstName = null;
        String lastName = null;
        int imageIndex = -1;

        SUT.setImageIndex(imageIndex);
        SUT.storeData(firstName, lastName);

        checkValues(firstName, lastName, imageIndex);
    }

    @Test
    public void isNameValid_true_setValidString() {
        String firstName = "Daniel";
        assertTrue(SUT.isNameValid(firstName));
    }

    @Test
    public void isNameValid_true_setValidWithSpecialString() {
        String firstName = "%Daniel%";
        assertTrue(SUT.isNameValid(firstName));
    }

    @Test
    public void isNameValid_false_setOneCharString() {
        String firstName = "D";
        assertFalse(SUT.isNameValid(firstName));
    }

    @Test
    public void isNameValid_false_setEmptyString() {
        String firstName = "";
        assertFalse(SUT.isNameValid(firstName));
    }

    @Test
    public void isNameValid_false_setNullString() {
        String firstName = null;
        assertFalse(SUT.isNameValid(firstName));
    }

    private void checkValues(String firstName, String lastName, int imageIndex) {
        assertEquals(firstName, sharedPreferences.getString(Constants.preferenceKey.FIRST_NAME, null));
        assertEquals(lastName, sharedPreferences.getString(Constants.preferenceKey.LAST_NAME, null));
        assertEquals(imageIndex, sharedPreferences.getInt(Constants.preferenceKey.IMAGE_INDEX, -1));
    }
}