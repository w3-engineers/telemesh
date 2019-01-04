package com.w3engineers.unicef.telemesh.ui.aboutus;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.App;
import com.w3engineers.unicef.telemesh.R;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [03-Oct-2018 at 12:29 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [03-Oct-2018 at 12:29 PM].
 * * --> <Second Editor> on [03-Oct-2018 at 12:29 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [03-Oct-2018 at 12:29 PM].
 * * --> <Second Reviewer> on [03-Oct-2018 at 12:29 PM].
 * * ============================================================================
 **/
public class AboutUsViewModel extends AndroidViewModel {

    public AboutUsViewModel(@NonNull Application application) {
        super(application);
    }

    public String getAppVersion() {

        try {
            PackageInfo pInfo = getApplication().getApplicationContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0);
            return getApplication().getApplicationContext().getString(R.string.app_version) + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

}
