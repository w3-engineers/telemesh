package com.w3engineers.unicef.telemesh.data.analytics;

import android.content.Context;
import android.os.AsyncTask;

import com.w3engineers.unicef.telemesh.data.analytics.callback.AnalyticsResponseCallback;
import com.w3engineers.unicef.telemesh.data.analytics.model.AppShareCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.model.NewNodeModel;
import com.w3engineers.unicef.telemesh.data.analytics.parseapi.ParseManager;

import java.util.List;
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/


public class AnalyticsApi {
    private static volatile AnalyticsApi ourInstance;
    private static final Object mutex = new Object();
    private byte analyticsType;
    private AnalyticsResponseCallback analyticsResponseCallback;

    private AnalyticsApi(Context context) {
        new InitParseManagerAsync(context).execute();
    }

    public synchronized static void init(Context context) {
        synchronized (mutex) {
            if (ourInstance == null)
                ourInstance = new AnalyticsApi(context);
        }
    }

    public static AnalyticsApi on() {
        return ourInstance;
    }

    public AnalyticsApi setAnalyticsType(byte analyticsType) {
        this.analyticsType = analyticsType;
        return this;
    }

    public AnalyticsApi setAnalyticsResponseCallback(AnalyticsResponseCallback analyticsResponseCallback) {
        this.analyticsResponseCallback = analyticsResponseCallback;
        return this;
    }

    public void saveMessageCount(MessageCountModel model) {
        AsyncTask.execute(() -> ParseManager.on()
                .setCallback(analyticsType, analyticsResponseCallback).saveMessageCount(model));
    }

    public void sendNewUserAnalytics(List<NewNodeModel> nodeList) {
        AsyncTask.execute(() -> ParseManager.on().setCallback(analyticsType, analyticsResponseCallback)
                .sendNewUserAnalytics(nodeList));
    }

    public void sendAppShareCount(List<AppShareCountModel> model) {
        AsyncTask.execute(() -> ParseManager.on().setCallback(analyticsType, analyticsResponseCallback)
                .sendAppShareCount(model));
    }
}
