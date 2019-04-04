package com.w3engineers.unicef.telemesh.ui.aboutus;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class AboutUsViewModel extends AndroidViewModel {

    public AboutUsViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public String getAppVersion() {

//        try {
//            PackageInfo pInfo = getApplication().getApplicationContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0);
//            return getApplication().getApplicationContext().getString(R.string.app_version) + pInfo.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        return getApplication().getApplicationContext().getString(R.string.app_version) + BuildConfig.VERSION_NAME;
    }

}
