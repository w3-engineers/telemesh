package com.w3engineers.unicef;

import android.content.Context;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.ui.inappshare.InAppShareWebController;
import com.w3engineers.unicef.util.helper.LanguageUtil;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [02-Nov-2018 at 6:21 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [02-Nov-2018 at 6:21 PM].
 * * --> <Second Editor> on [02-Nov-2018 at 6:21 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [02-Nov-2018 at 6:21 PM].
 * * --> <Second Reviewer> on [02-Nov-2018 at 6:21 PM].
 * * ============================================================================
 **/
public class TeleMeshApplication extends App {

    private static String DEFAULT_LANGUAGE = "en";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // Set app language based on user
        String language = SharedPref.getSharedPref(base).read(Constants.preferenceKey.APP_LANGUAGE);
        if (language.equals("")){
            language = DEFAULT_LANGUAGE;
        }
        LanguageUtil.setAppLanguage(base, language);
        InAppShareWebController.getInAppShareWebController().initContext(base);
    }

}
