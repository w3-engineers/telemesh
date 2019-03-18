package com.w3engineers.unicef.telemesh.ui.inappshare;

import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [13-Jul-2018 at 11:53 AM].
 * * Email: mimosaha@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: InstantShare.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [13-Jul-2018 at 11:53 AM].
 * * --> <Second Editor> on [13-Jul-2018 at 11:53 AM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [13-Jul-2018 at 11:53 AM].
 * * --> <Second Reviewer> on [13-Jul-2018 at 11:53 AM].
 * * ============================================================================
 **/
public class WebUpdater {

    private static WebUpdater webUpdater;
    private Context context;
    private String HTML_DIRECTORY_ROOT = "html/";

    public static WebUpdater getWebUpdater() {
        if (webUpdater == null)
            webUpdater = new WebUpdater();
        return webUpdater;
    }

    public void initContext(Context context) {
        this.context = context;
    }

    public InputStream getWebFile(String contentPath) {

        try {
            String HTML_INDEX_FILE_NAME = "index.html";
            InputStream fileInputStream = context.getAssets().open(HTML_DIRECTORY_ROOT + HTML_INDEX_FILE_NAME);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            String aDataRow = "", aBuffer = "";

            while ((aDataRow = bufferedReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
            }
            bufferedReader.close();

            String newExtension = "." + getMime(contentPath);
            String oldExtension = ".apk";
            String newInfo = aBuffer.replace(oldExtension, newExtension);

            return new ByteArrayInputStream(newInfo.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream getWebSupportFile(String uri) {
        try {
            return context.getAssets().open(HTML_DIRECTORY_ROOT + uri.substring(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMime(String url) {
        int dot = url.lastIndexOf('.');
        if (dot >= 0)
            return url.substring(dot + 1).toLowerCase();

        return null;
    }
}
