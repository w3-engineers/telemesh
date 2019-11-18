package com.w3engineers.unicef;

import android.content.Context;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.util.MeshApp;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsApi;
import com.w3engineers.unicef.telemesh.data.analytics.CredentialHolder;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.util.helper.ExceptionTracker;
import com.w3engineers.unicef.util.helper.LanguageUtil;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class TeleMeshApplication extends MeshApp {

    @Override
    protected void attachBaseContext(@NonNull Context base) {
        super.attachBaseContext(base);
        // Set app language based on user
        String language = SharedPref.getSharedPref(base).read(Constants.preferenceKey.APP_LANGUAGE);
        if (language.equals("")) {
            language = "en";
        }
        LanguageUtil.setAppLanguage(base, language);

        initCredential();
//        LogProcessUtil.getInstance().loadAllLogs();
        AnalyticsApi.init(base);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionTracker());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.getInstance();
    }

    private void initCredential() {
        CredentialHolder.getInStance().init(BuildConfig.PARSE_APP_ID, "", BuildConfig.PARSE_URL);
    }
}
