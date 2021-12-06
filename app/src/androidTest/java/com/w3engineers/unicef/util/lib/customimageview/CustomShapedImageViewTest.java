package com.w3engineers.unicef.util.lib.customimageview;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.util.lib.circle_checkbox.SmoothCheckBox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CustomShapedImageViewTest {

    private Context mContext;
    private static final String KEY_INSTANCE_STATE = "InstanceState";

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
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
