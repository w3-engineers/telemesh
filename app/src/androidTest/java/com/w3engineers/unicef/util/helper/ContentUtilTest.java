package com.w3engineers.unicef.util.helper;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

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
    private String videoFilePath = "file:///android_asset/sample_vide.mp4";

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

        int mediaType = contentUtil.getContentMessageType(sampleContentPath);

        assertEquals(mediaType, Constants.MessageType.TYPE_DEFAULT);
        addDelay(300);

        String resultTime = contentUtil.getMediaTime(4680000);
        assertEquals("1:18:00", resultTime);

        addDelay(300);

        String fileName = ContentUtil.getFileNameFromURL(videoFilePath);
        assertEquals("sample_vide.mp4", fileName);
        addDelay(300);

        long duration = contentUtil.getMediaDuration(videoFilePath);
        assertEquals(0, duration);
        addDelay(300);

        String filePath = contentUtil.getContentFromUrl("https://dashboard.telemesh.net/message/download?filename=myfile_1624623314123-467515276.jpeg");

        assertFalse(TextUtils.isEmpty(filePath));

        addDelay(3000);

        contentUtil.getFilePathFromUri(Uri.parse("content://media/external/test/file.mp4"));

        addDelay(1000);


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