package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [06-Nov-2018 at 3:06 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [06-Nov-2018 at 3:06 PM].
 * * --> <Second Editor> on [06-Nov-2018 at 3:06 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [06-Nov-2018 at 3:06 PM].
 * * --> <Second Reviewer> on [06-Nov-2018 at 3:06 PM].
 * * ============================================================================
 **/
public class LanguageUtil {

    public static void setAppLanguage(@NonNull Context context, @NonNull String language){

        Locale myLocale = new Locale(language);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

}
