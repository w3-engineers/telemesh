package com.w3engineers.unicef.telemesh.ui.settings;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RightMeshDataSource;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.util.helper.LanguageUtil;

import java.util.Locale;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [08-Oct-2018 at 3:14 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [08-Oct-2018 at 3:14 PM].
 * * --> <Second Editor> on [08-Oct-2018 at 3:14 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [08-Oct-2018 at 3:14 PM].
 * * --> <Second Reviewer> on [08-Oct-2018 at 3:14 PM].
 * * ============================================================================
 **/
public class SettingsViewModel extends AndroidViewModel {


    public SettingsViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean getCheckedStatus() {
        return SharedPref.getSharedPref(getApplication().getApplicationContext())
                .readBoolean(Constants.preferenceKey.IS_NOTIFICATION_ENABLED);
    }

    public void onCheckedChanged(boolean checked) {
        SharedPref.getSharedPref(getApplication().getApplicationContext())
                .write(Constants.preferenceKey.IS_NOTIFICATION_ENABLED, checked);
    }

    @NonNull
    public String getAppLanguage() {

        String language = SharedPref.getSharedPref(App.getContext()).read(Constants.preferenceKey.APP_LANGUAGE_DISPLAY);
        return !language.equals("") ? language : App.getContext().getString(R.string.demo_language);
    }

    public void setLocale(@NonNull String lang, @Nullable String landDisplay) {

        SharedPref.getSharedPref(getApplication().getApplicationContext()).write(Constants.preferenceKey.APP_LANGUAGE, lang);
        SharedPref.getSharedPref(getApplication().getApplicationContext()).write(Constants.preferenceKey.APP_LANGUAGE_DISPLAY, landDisplay);

        LanguageUtil.setAppLanguage(getApplication().getApplicationContext(), lang);
    }

//     This api is unused
//    public void openWallet() {
//        RightMeshDataSource.getRmDataSource().openRmSettings();
//    }
}
