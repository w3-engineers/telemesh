package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.text.SpannableString;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class WalletAddressHelperTest {

    private String meshID;
    private Context context;

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setup() {
        meshID = "0x550de922bec427fc1b279944e47451a89a4f7cag";
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void defaultWalletAddressCreateTest() {
        addDelay(500);

       // WalletAddressHelper.writeDefaultAddress(meshID, context);

//        addDelay(2000);

       // SpannableString content = WalletAddressHelper.getWalletSpannableString(context);

//        addDelay(2000);

       // assertTrue(content.length() > 0);
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}