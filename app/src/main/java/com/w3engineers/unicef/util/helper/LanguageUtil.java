package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;

import java.util.Locale;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class LanguageUtil {

    public static void setAppLanguage(@NonNull Context context, @NonNull String language){

        Locale myLocale = new Locale(language);
        Locale.setDefault(myLocale);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;
        conf.setLayoutDirection(myLocale);
        res.updateConfiguration(conf, dm);

    }

    public static String getString(int resId){
       return TeleMeshApplication.getContext().getResources().getString(resId);
    }
}
