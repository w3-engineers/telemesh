package com.w3engineers.unicef.telemesh.data.analytics.parseapi;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.AnalyticsResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.FileUploadResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.model.AppShareCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Purpose: This class is responsible to send or receive data
 * from parse server.
 * ============================================================================
 */


public class ParseManager {
    private static volatile ParseManager sInstance;
    private static final Object mutex = new Object();
    private static Context mContext;
    private static String TAG = ParseManager.class.getName();
    private byte analyticsType;
    private AnalyticsResponseCallback analyticsResponseCallback;

    public synchronized static void init(Context context, String serverUrl, String appId, String clientKey) {

        synchronized (mutex) {
            if (sInstance == null) sInstance = new ParseManager();
        }
        mContext = context;
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(appId)
                .clientKey(clientKey)
                .server(serverUrl)
                .enableLocalDataStore()
                .build()
        );
    }

    public static ParseManager on() {
        return sInstance;
    }

    public ParseManager setCallback(byte analyticsType, AnalyticsResponseCallback analyticsResponseCallback) {
        this.analyticsResponseCallback = analyticsResponseCallback;
        this.analyticsType = analyticsType;
        return this;
    }

    /**
     * This method is responsible to send Message count analytics data
     * to server of each user
     * And here we user saveEventually to handle poor connection
     * or no connection (Internet)
     *
     * @param model {@link MessageCountModel}
     */
    public void saveMessageCount(MessageCountModel model) {
        ParseObject parseObject = new ParseMapper().MessageCountToParse(model);

        parseObject.saveEventually(e -> {
            sendResponse(e == null);
        });

    }

    public void sendNewUserAnalytics(List<NewNodeModel> nodeList) {
        ParseObject newUserList = new ParseMapper().NewNodeToParse(nodeList);
        newUserList.saveEventually(e -> {
            sendResponse(e == null);
        });
    }

    public void sendAppShareCount(List<AppShareCountModel> model) {
        ParseObject object = new ParseMapper().AppShareCountToParse(model);
        object.getUpdatedAt();
        object.saveEventually(e -> sendResponse(e == null));
    }

    public void sendLogFileInServer(File logFile, String userId, String deviceName, FileUploadResponseCallback callback) {

        int size = (int) logFile.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(logFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ParseFile file = new ParseFile(logFile, "txt");
        ParseFile file = new ParseFile("meshLog.txt", bytes);

        file.saveInBackground((SaveCallback) e -> {
            if (e == null) {

                String osVersion = String.valueOf(Build.VERSION.RELEASE);

                ParseObject object = new ParseObject(ParseConstant.MeshLog.TABLE);
                object.put(ParseConstant.MeshLog.USER_ID, userId);
                object.put(ParseConstant.MeshLog.LOG_FILE, file);
                object.put(ParseConstant.MeshLog.DEVICE_NAME, deviceName);
                object.put(ParseConstant.MeshLog.DEVICE_OS, osVersion);

                object.saveInBackground(e1 -> {
                    if (e1 == null) {
                        if (callback != null) {
                            callback.onGetMeshLogUploadResponse(true, logFile.getName());
                        }
                        Log.d("ParseFileUpload", "Save done ");
                    } else {
                        if (callback != null) {
                            callback.onGetMeshLogUploadResponse(false, "");
                        }
                        Log.e("ParseFileUpload", "Error: " + e1.getMessage());
                    }
                });
            } else {
                if (callback != null) {
                    callback.onGetMeshLogUploadResponse(false, "");
                }
                Log.e("ParseFileUpload", "Error: " + e.getMessage());
            }
        });
    }

    private void sendResponse(boolean isSuccess) {
        if (analyticsResponseCallback != null) {
            analyticsResponseCallback.response(isSuccess, analyticsType);
        }
    }
}
