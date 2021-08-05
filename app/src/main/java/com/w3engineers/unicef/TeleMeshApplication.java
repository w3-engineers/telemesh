package com.w3engineers.unicef;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.mesh.util.MeshApp;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsApi;
import com.w3engineers.unicef.telemesh.data.analytics.CredentialHolder;
import com.w3engineers.unicef.telemesh.data.di.ApplicationComponent;


import com.w3engineers.unicef.telemesh.data.di.DaggerApplicationComponent;
import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.AppDatabase;
import com.w3engineers.unicef.util.helper.CommonUtil;
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

    // Reference to the application graph that is used across the whole app
    public ApplicationComponent appComponent = DaggerApplicationComponent.create();


    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        AppDatabase.getInstance();
        // Set app language based on user

        if (!BuildConfig.DEBUG) {
            if (CommonUtil.isEmulator())
                return;
        }

        String language = SharedPref.read(Constants.preferenceKey.APP_LANGUAGE);
        if (TextUtils.isEmpty(language)) {
            language = "en";
        }
        LanguageUtil.setAppLanguage(mContext, language);

        initCredential();

        AnalyticsApi.init(mContext);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionTracker());

    }

    private void initCredential() {
        CredentialHolder.getInStance().init(AppCredentials.getInstance().getParseAppId(), "", AppCredentials.getInstance().getParseUrl());
    }

    public static Context getContext() {
        return mContext;
    }
}
