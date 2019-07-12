package com.w3engineers.unicef.telemesh.data.analytics.parseapi;

import android.content.Context;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.w3engineers.unicef.telemesh.data.analytics.callback.AnalyticsCallback;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;

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

    /**
     * This method is responsible to send Message count analytics data
     * to server of each user
     * And here we user saveEventually to handle poor connection
     * or no connection (Internet)
     *
     * @param model {@link MessageCountModel}
     */
    public void saveMessageCount(MessageCountModel model, AnalyticsCallback callback) {
        ParseObject parseObject = new ParseMapper().MessageCountToParse(model);

        parseObject.saveEventually(e -> {
            if (e == null) {
                Log.d(TAG, "Data Save complete");
            } else {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        });

    }

    public void sendNewUserAnalytics(List<NewNodeModel> nodeList) {
        ParseObject newUserList = new ParseMapper().NewNodeToParse(nodeList);
        newUserList.saveEventually(e -> {
            if (e == null) {
                Log.d(TAG, "Data Save complete");
            } else {
                Log.e(TAG, "Error: " + e.getMessage());
            }
        });
    }
}
