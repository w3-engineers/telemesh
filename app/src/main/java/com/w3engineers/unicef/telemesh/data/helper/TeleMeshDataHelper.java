package com.w3engineers.unicef.telemesh.data.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.App;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class TeleMeshDataHelper {

    @SuppressLint("StaticFieldLeak")
    private static TeleMeshDataHelper teleMeshDataHelper = null;
    private Context context;

    private TeleMeshDataHelper() {
        context = App.getContext();
    }

    @NonNull
    public static TeleMeshDataHelper getInstance() {
        if (teleMeshDataHelper == null) {
            teleMeshDataHelper = new TeleMeshDataHelper();
        }
        return teleMeshDataHelper;
    }

    public int getAvatarImage(int imageIndex) {
        return context.getResources().getIdentifier(Constants.drawables.AVATAR_IMAGE + imageIndex,
                Constants.drawables.AVATAR_DRAWABLE_DIRECTORY, context.getPackageName());
    }

}
