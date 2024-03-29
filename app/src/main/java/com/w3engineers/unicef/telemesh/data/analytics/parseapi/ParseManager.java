package com.w3engineers.unicef.telemesh.data.analytics.parseapi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.AnalyticsResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.FeedbackSendCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.FileUploadResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.GroupCountSendCallback;
import com.w3engineers.unicef.telemesh.data.analytics.model.AppShareCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.FeedbackParseModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.GroupCountParseModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;
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
    @SuppressLint("StaticFieldLeak")
    private static volatile ParseManager sInstance;
    private static final Object mutex = new Object();
    @SuppressLint("StaticFieldLeak")
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
                    } else {
                        if (callback != null) {
                            callback.onGetMeshLogUploadResponse(false, "");
                        }
                    }
                });
            } else {
                if (callback != null) {
                    callback.onGetMeshLogUploadResponse(false, "");
                }
            }
        });
    }

    public void sendFeedback(FeedbackParseModel model, FeedbackSendCallback callback) {
        ParseObject parseObject = new ParseObject(ParseConstant.Feedback.TABLE);
        parseObject.put(ParseConstant.Feedback.USER_ID, model.getUserId());
        parseObject.put(ParseConstant.Feedback.USER_NAME, model.getUserName());
        parseObject.put(ParseConstant.Feedback.USER_FEEDBACK, model.getFeedback());
        parseObject.put(ParseConstant.Feedback.FEEDBACK_ID, model.getFeedbackId());

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(ParseConstant.Feedback.TABLE);

        parseQuery.whereEqualTo(ParseConstant.Feedback.FEEDBACK_ID, model.getFeedbackId());
        parseQuery.findInBackground((objects, e) -> {
            if (e == null) {
                if (objects == null || objects.isEmpty()) {
                    parseObject.saveInBackground(e1 -> {
                        if (callback != null) {
                            if (e1 == null) {
                                callback.onGetFeedbackSendResponse(true, model);
                            } else {
                                Timber.tag("FeedbackTest").e("Feedback send Failed: %s", e1.getMessage());
                            }
                        }
                    });
                } else {
                    Timber.tag("FeedbackTest").e("Feed back already exist");
                }
            } else {
                Timber.tag("FeedbackTest").e("Feed back query Error: %s", e.getMessage());
            }
        });

    }

    private ArrayList<ParseObject> removeExistingGroups(ArrayList<ParseObject> parseModels, ArrayList<String> existingGroupIds){
        for (int i = parseModels.size() - 1; i > -1; i--) {
            ParseObject obj = parseModels.get(i);
            if (Arrays.toString(existingGroupIds.toArray()).contains(obj.getString(ParseConstant.GroupCount.GROUP_ID))) {
                parseModels.remove(obj);
            }
        }
        return parseModels;
    }

    public void sendGroupCount(ArrayList<GroupCountParseModel> modelList, GroupCountSendCallback callback) {

        ArrayList<ParseObject> parseModels = new ArrayList<>();
        ArrayList<String> groupIds = new ArrayList<>();
        for (GroupCountParseModel model : modelList) {
            ParseObject parseObject = new ParseMapper().GroupCountToParse(model);
            parseModels.add(parseObject);
            groupIds.add(model.getGroupId());
        }

        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(ParseConstant.GroupCount.TABLE);

        parseQuery.whereContainedIn(ParseConstant.GroupCount.GROUP_ID, groupIds);

        parseQuery.findInBackground((objects, e) -> {
            if (e == null) {
                ArrayList<String> existingGroupIds = new ArrayList<>();
                for (ParseObject object: objects) {
                    String groupId = object.getString(ParseConstant.GroupCount.GROUP_ID);
                    existingGroupIds.add(groupId);
                }
                if (!existingGroupIds.isEmpty()) {
                    removeExistingGroups(parseModels, existingGroupIds);
                }

                if (parseModels.isEmpty()) {
                    if (callback != null) {
                        callback.onGetGroupCountSendResponse(true, modelList);
                    }
                    Timber.tag("GroupCount").e("GroupCount is empty");
                } else {
                    ParseObject.saveAllInBackground(parseModels, e1 -> {
                        if (e1 == null) {
                            if (callback != null) {
                                callback.onGetGroupCountSendResponse(true, modelList);
                            }
                        } else {
                            Timber.tag("GroupCount").e("GroupCount send Failed: %s", e1.getMessage());
                        }
                    });
                }
            } else {
                Timber.tag("GroupCount").e("GroupCount query Error: %s", e.getMessage());
            }
        });
    }


    private void sendResponse(boolean isSuccess) {
        if (analyticsResponseCallback != null) {
            analyticsResponseCallback.response(isSuccess, analyticsType);
        }
    }
}
