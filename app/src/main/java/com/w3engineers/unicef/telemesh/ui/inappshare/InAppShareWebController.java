package com.w3engineers.unicef.telemesh.ui.inappshare;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
public class InAppShareWebController {

    private static InAppShareWebController inAppShareWebController = new InAppShareWebController();
    private Context context;
    private String HTML_DIRECTORY_ROOT = "html/";

    /**
     * get web controller instance
     * @return - singleton instance
     */
    public static InAppShareWebController getInAppShareWebController() {
        return inAppShareWebController;
    }

    /**
     * Access html assets required a context
     * @param context - view context or application context
     */
    public void initContext(Context context) {
        this.context = context;
    }

    /**
     * Send a html page input stream
     * @return - download option html view as input stream
     */
    @Nullable
    public InputStream getWebFile() {

        try {
            String HTML_INDEX_FILE_NAME = "index.html";
            return context.getAssets().open(HTML_DIRECTORY_ROOT + HTML_INDEX_FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send design and style related files as a input stream
     * @param uri - input a uri type. e.g. css, png, icon
     * @return - convert css and design file to input stream format
     */
    @Nullable
    public InputStream getWebSupportFile(@NonNull String uri) {
        try {
            return context.getAssets().open(HTML_DIRECTORY_ROOT + uri.substring(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
