package com.w3engineers.unicef.telemesh.ui.userprofile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [02-Oct-2018 at 4:25 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [02-Oct-2018 at 4:25 PM].
 * * --> <Second Editor> on [02-Oct-2018 at 4:25 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [02-Oct-2018 at 4:25 PM].
 * * --> <Second Reviewer> on [02-Oct-2018 at 4:25 PM].
 * * ============================================================================
 **/
public class UserProfileViewModel extends AndroidViewModel {


    public UserProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public String getUserName() {
        return SharedPref.getSharedPref(getApplication().getApplicationContext())
                .read(Constants.preferenceKey.FIRST_NAME) + " " +
                SharedPref.getSharedPref(getApplication().getApplicationContext())
                        .read(Constants.preferenceKey.LAST_NAME);
    }

    public int getProfileImage() {
        return getApplication().getApplicationContext().getResources().getIdentifier(
                Constants.drawables.AVATER_IMAGE + SharedPref.getSharedPref(
                        getApplication().getApplicationContext()).readInt(Constants.preferenceKey.IMAGE_INDEX),
                Constants.drawables.AVATER_DRAWABLE_DIRECTORY, getApplication().getApplicationContext().getPackageName());
    }
}
