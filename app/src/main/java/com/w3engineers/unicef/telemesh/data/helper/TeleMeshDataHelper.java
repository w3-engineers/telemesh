package com.w3engineers.unicef.telemesh.data.helper;

import android.content.Context;

import com.w3engineers.ext.strom.App;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [08-Oct-2018 at 5:46 PM].
 * * Email: mimosaha@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [08-Oct-2018 at 5:46 PM].
 * * --> <Second Editor> on [08-Oct-2018 at 5:46 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [08-Oct-2018 at 5:46 PM].
 * * --> <Second Reviewer> on [08-Oct-2018 at 5:46 PM].
 * * ============================================================================
 **/
public class TeleMeshDataHelper {

    private static TeleMeshDataHelper teleMeshDataHelper = null;
    private Context context;

    private TeleMeshDataHelper() {
        context = App.getContext();
    }

    public static TeleMeshDataHelper getInstance() {
        if (teleMeshDataHelper == null) {
            teleMeshDataHelper = new TeleMeshDataHelper();
        }
        return teleMeshDataHelper;
    }

    public int getAvatarImage(int imageIndex) {
        return context.getResources().getIdentifier(Constants.drawables.AVATER_IMAGE + imageIndex,
                Constants.drawables.AVATER_DRAWABLE_DIRECTORY, context.getPackageName());
    }



}
