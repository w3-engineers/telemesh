package com.w3engineers.unicef.util.lib.circle_checkbox;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class SmoothCheckBoxTest {
    private Context mContext;
    private static final String KEY_INSTANCE_STATE = "InstanceState";

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
    }

    @Test
    public void stateSaveTest() {

        addDelay();

        SmoothCheckBox entity = new SmoothCheckBox(mContext);

        entity.setChecked(true);

        Parcelable parcelable = entity.onSaveInstanceState();

        entity.onRestoreInstanceState(parcelable);

        Bundle bundle = (Bundle) parcelable;

        assertTrue(bundle.getBoolean(KEY_INSTANCE_STATE));

        addDelay();
    }

    @Test
    public void uncheckEventOfBoxTest(){
        addDelay();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SmoothCheckBox entity = new SmoothCheckBox(mContext);

            entity.setChecked(false,true);

            assertFalse(entity.isChecked());

            addDelay();
            addDelay();
            addDelay();
        },1);

    }

    private void addDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}