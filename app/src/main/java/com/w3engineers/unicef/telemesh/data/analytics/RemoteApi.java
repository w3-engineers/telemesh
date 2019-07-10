package com.w3engineers.unicef.telemesh.data.analytics;

import android.content.Context;
import android.os.AsyncTask;

import com.w3engineers.unicef.telemesh.data.helper.InitManagerAsync;
import com.w3engineers.unicef.telemesh.data.analytics.model.MessageCountModel;
import com.w3engineers.unicef.telemesh.data.analytics.parseapi.ParseManager;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * Purpose: This is main api platform of Remote call.
 * It is not depend on Parse or Manager class.
 * If any change of manager layer one manger fully replaced
 * here we have to change manager class
 *
 * ============================================================================
 */


public class RemoteApi {
    private static volatile RemoteApi ourInstance;
    private static final Object mutex = new Object();

    private RemoteApi(Context context) {
        new InitManagerAsync(context).execute();
    }

    public synchronized static void init(Context context) {
        synchronized (mutex) {
            if (ourInstance == null)
                ourInstance = new RemoteApi(context);
        }
    }

    public static RemoteApi on() {
        return ourInstance;
    }

    public void saveMessageCount(MessageCountModel model) {
        AsyncTask.execute(() -> ParseManager.on().saveMessageCount(model));
    }
}
