package com.w3engineers.unicef.telemesh.ui.createuser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.util.helper.StatusHelper;

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
    private CreateUserViewModel SUT;

    @Rule
    public ActivityTestRule<CreateUserActivity> rule  = new ActivityTestRule<>(CreateUserActivity.class);

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getTargetContext();
        SUT = new CreateUserViewModel(rule.getActivity().getApplication());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @After
    public void tearDown() {
    }


    @Test
    public void storeData_storeNameNull_getNameNull() {
        String firstName = null;
        String lastName = null;
        int imageIndex = -1;

        SUT.setImageIndex(imageIndex);
       // SUT.storeData(firstName);

        checkValues(firstName, imageIndex);

        StatusHelper.out("Test executed");
    }

    @Test
    public void isNameValid_true_setValidString() {
        String firstName = "Daniel";
        assertTrue(SUT.isNameValid(firstName));

        StatusHelper.out("Test executed");
    }

    @Test
    public void storeData_true_setValidData() {
        String firstName = "Daniel";
        String lastName = "Craig";
        assertTrue(SUT.storeData(firstName,lastName));

        StatusHelper.out("Test executed");
    }

    private void checkValues(String firstName, int imageIndex) {
        assertEquals(firstName, sharedPreferences.getString(Constants.preferenceKey.USER_NAME, null));
        assertEquals(imageIndex, sharedPreferences.getInt(Constants.preferenceKey.IMAGE_INDEX, -1));
    }
}