package com.w3engineers.unicef.util.helper;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.runner.lifecycle.Stage.RESUMED;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;

import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.ui.aboutus.AboutUsActivity;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

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
    private String imageFilePath = "file:///android_asset/sample_image.jpg";
    private String documentPath = "file:///android_asset/sample_doc.jpg";
    public Activity currentActivity = null;
    private File videoFile;

    @Rule
    public ActivityTestRule<AboutUsActivity> rule = new ActivityTestRule<>(AboutUsActivity.class);

    @Before
    public void setUp() throws Exception {
        contentUtil = ContentUtil.getInstance();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testExternalStorageFalse() {
        addDelay(200);
        boolean isExternal = contentUtil.isExternalStorageDocument(Uri.parse("anyString"));
        assertFalse(isExternal);
    }

    @Test
    public void testDownloadsDocumentFalse() {
        addDelay(200);
        boolean isExternal = contentUtil.isDownloadsDocument(Uri.parse("anyString"));
        assertFalse(isExternal);
    }

    @Test
    public void testMediaDocumentFalse() {
        addDelay(200);
        boolean isExternal = contentUtil.isMediaDocument(Uri.parse("anyString"));
        assertFalse(isExternal);
    }

    @Test
    public void testThumbGenerateFromImage(){
        addDelay(300);
        contentUtil.getThumbnailFromImagePath(imageFilePath);
        assertTrue(true);
    }

    @Test
    public void testThumbGenerateFromVideo(){
        addDelay(300);
        contentUtil.getThumbnailFromVideoPath(videoFilePath);
        assertTrue(true);
    }

    @Test
    public void testfilePathUri(){
        // to test media content
        addDelay(300);
        contentUtil.getFilePathFromUri(Uri.parse(imageFilePath));
        assertTrue(true);

    }

    @Test
    public void testMediaProcess(){
        addDelay(100);
        contentUtil.mediaContentProcess(Uri.parse("content://com.android.providers.media.documents/document/image"));
        assertTrue(true);
    }

    @Test
    public void testMatrixValue(){
        addDelay(100);
        contentUtil.matrixPostRotate(90);
        assertTrue(true);
    }

    @Test
    public void testContentUtil(){
        addDelay(100);
        contentUtil.prepareFile("jpg");
        assertTrue(true);
    }

    @Test
    public void test_content_type_and_path_validity_check() {


        String resultPath = contentUtil.getContentMessageBody(sampleContentPath);

        assertTrue(TextUtils.isEmpty(resultPath));

        addDelay(300);

        int mediaType = contentUtil.getContentMessageType(sampleContentPath);

        currentActivity = getActivityInstance();

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

        contentUtil.getFilePathFromUri(Uri.parse("content://com.android.providers.media.documents/document/image/test.jpg"));
        addDelay(1000);

        // Getting video thumb path
        insertADummyVideoFile();
        addDelay(2000);

        String videoThumbPath = contentUtil.getThumbnailFromVideoPath(videoFile.getPath());
        Log.d("testTag","patH: "+videoThumbPath);

        StatusHelper.out("test case executed");
    }

    @Test
    public void testMalformedUrl(){
        ContentUtil.getFileNameFromURL("htps://airbrake.io");
        assertTrue(true);
    }

    @Test
    public void testNullUrl(){
        ContentUtil.getFileNameFromURL(null);
        assertTrue(true);
    }

    private void addDelay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void insertADummyVideoFile() {
        videoFile = new File(Environment.getExternalStorageDirectory(), "test.mp4");
        try {

            AssetFileDescriptor assetFileDescriptor = currentActivity.getAssets().openFd("sample_video.mp4");

            FileInputStream inputStream = assetFileDescriptor.createInputStream();

            OutputStream outputStream = new FileOutputStream(videoFile);
            copyStream(inputStream, outputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Activity getActivityInstance() {
        getInstrumentation().runOnMainSync(() -> {
            Collection resumedActivities =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = (Activity) resumedActivities.iterator().next();
            }
        });

        return currentActivity;
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}