package com.w3engineers.unicef;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.util.MeshApp;
import com.w3engineers.mesh.util.MeshLog;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.analytics.AnalyticsApi;
import com.w3engineers.unicef.telemesh.data.analytics.CredentialHolder;
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

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    protected void attachBaseContext(@NonNull Context base) {
        super.attachBaseContext(base);
        mContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.getInstance();
        // Set app language based on user

        if (!BuildConfig.DEBUG) {
            if (CommonUtil.isEmulator())
                return;
        }

        String language = SharedPref.getSharedPref(mContext).read(Constants.preferenceKey.APP_LANGUAGE);
        if (TextUtils.isEmpty(language)) {
            language = "en";
        }
        LanguageUtil.setAppLanguage(mContext, language);

        initCredential();

        AnalyticsApi.init(mContext);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionTracker());
        Toaster.init(R.color.colorPrimary);

        /*if (BuildConfig.DEBUG) {
            Log.e("biuld_type", "debug build");
            MeshLog.i("from my self");
            String language = SharedPref.getSharedPref(mContext).read(Constants.preferenceKey.APP_LANGUAGE);
            if (TextUtils.isEmpty(language)) {
                language = "en";
            }
            LanguageUtil.setAppLanguage(mContext, language);

            initCredential();

            AnalyticsApi.init(mContext);
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionTracker());
            Toaster.init(R.color.colorPrimary);
        } else {
            if (!CommonUtil.isEmulator()) {
                Log.e("biuld_type", "relese build");
                String language = SharedPref.getSharedPref(mContext).read(Constants.preferenceKey.APP_LANGUAGE);
                if (TextUtils.isEmpty(language)) {
                    language = "en";
                }
                LanguageUtil.setAppLanguage(mContext, language);

                initCredential();

                AnalyticsApi.init(mContext);
                Thread.setDefaultUncaughtExceptionHandler(new ExceptionTracker());
                Toaster.init(R.color.colorPrimary);
            }
        }*/
    }

    private void initCredential() {
        CredentialHolder.getInStance().init(AppCredentials.getInstance().getParseAppId(), "", AppCredentials.getInstance().getParseUrl());
    }

    public static Context getContext() {
        return mContext;
    }
}
