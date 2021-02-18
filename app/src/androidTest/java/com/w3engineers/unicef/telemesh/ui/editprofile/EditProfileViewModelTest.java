package com.w3engineers.unicef.telemesh.ui.editprofile;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
@RunWith(AndroidJUnit4.class)
public class EditProfileViewModelTest {

    private EditProfileViewModel SUT;

    @Rule
    public ActivityTestRule<EditProfileActivity> rule  = new ActivityTestRule<>(EditProfileActivity.class);

    @Before
    public void setUp() {
        SUT = new EditProfileViewModel(rule.getActivity().getApplication());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void storeData_storeNameNull_getNameNull() {
        String firstName = "Daniel";
        int imageIndex = -1;

        SUT.setImageIndex(imageIndex);

        assertTrue(SUT.storeData(firstName));
    }

    @Test
    public void isNameValid_true_setValidString() {
        String firstName = "Daniel";
        assertTrue(SUT.isNameValid(firstName));

        addDelay(500);

        SUT.sendUserInfoToAll();

        addDelay(2000);
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}