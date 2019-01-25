package com.w3engineers.unicef.telemesh.ui.userprofile;

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
import static org.junit.Assert.assertNotEquals;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [25-Jan-2019 at 3:42 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [25-Jan-2019 at 3:42 PM].
 * --> <Second Editor> on [25-Jan-2019 at 3:42 PM].
 * Reviewed by :
 * --> <First Reviewer> on [25-Jan-2019 at 3:42 PM].
 * --> <Second Reviewer> on [25-Jan-2019 at 3:42 PM].
 * ============================================================================
 **/

@RunWith(AndroidJUnit4.class)
public class UserProfileViewModelTest {

    private SharedPreferences sharedPreferences;
    UserProfileViewModel SUT;

    @Rule
    public ActivityTestRule<UserProfileActivity> rule  = new ActivityTestRule<>(UserProfileActivity.class);

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        SUT = new UserProfileViewModel(rule.getActivity().getApplication());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @After
    public void tearDown() throws Exception {

    }

    /*@Test
    public void getUserName_match_setValidName() {
        String firstName = "Daniel";
        String lastName = "Alvez";

        String fullName = firstName + " " + lastName;

        setValueInPref(firstName, lastName);
        assertEquals(fullName, SUT.getUserName());
    }

    @Test
    public void getUserName_match_setValidNameWithSpecialChar() {
        String firstName = "%Daniel%";
        String lastName = "%Alvez%";

        String fullName = firstName + " " + lastName;

        setValueInPref(firstName, lastName);
        assertEquals(fullName, SUT.getUserName());
    }

    @Test
    public void getUserName_match_setEmptyName() {
        String firstName = "";
        String lastName = "";

        String fullName = firstName + " " + lastName;

        setValueInPref(firstName, lastName);
        assertEquals(fullName, SUT.getUserName());
    }

    @Test
    public void getUserName_match_setNullName() {
        String firstName = null;
        String lastName = null;

        String fullName = firstName + " " + lastName;

        setValueInPref(firstName, lastName);
        // using assertNotEquals
        // because in app side we are used default value "" when we get string data from SharedPreference
        assertNotEquals(fullName, SUT.getUserName());
    }

    private void setValueInPref(String firstName, String lastName) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.preferenceKey.FIRST_NAME, firstName);
        editor.putString(Constants.preferenceKey.LAST_NAME, lastName);
        editor.commit();
    }*/

}