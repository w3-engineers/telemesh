package com.w3engineers.unicef.util.helper;

import android.text.TextUtils;

import androidx.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@RunWith(AndroidJUnit4.class)
public class ContentUtilTest extends TestCase {

    private ContentUtil contentUtil;
    private String sampleContentPath = "file://test.abc";

    @Before
    public void setUp() throws Exception {
        contentUtil = ContentUtil.getInstance();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test_content_type_and_path_validity_check() {
        String resultPath = contentUtil.getContentMessageBody(sampleContentPath);

        assertTrue(TextUtils.isEmpty(resultPath));

        addDelay(300);

        String resultTime = contentUtil.getMediaTime(4680000);
        assertEquals("1:18:00", resultTime);

        addDelay(300);

        String fileName = ContentUtil.getFileNameFromURL("file:///android_asset/sample_vide.mp4");
        assertEquals("sample_vide.mp4", fileName);
        addDelay(300);

        StatusHelper.out("test case executed");
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}