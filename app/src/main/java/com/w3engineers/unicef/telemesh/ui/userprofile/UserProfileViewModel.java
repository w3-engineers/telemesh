package com.w3engineers.unicef.telemesh.ui.userprofile;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class UserProfileViewModel extends AndroidViewModel {


    public UserProfileViewModel(@NonNull Application application) {
        super(application);
    }

//    public String getUserName() {
//        return SharedPref.getSharedPref(getApplication().getApplicationContext())
//                .read(Constants.preferenceKey.USER_NAME) + " " +
//                SharedPref.getSharedPref(getApplication().getApplicationContext())
//                        .read(Constants.preferenceKey.LAST_NAME);
//    }
//
//    public int getProfileImage() {
//        return getApplication().getApplicationContext().getResources().getIdentifier(
//                Constants.drawables.AVATAR_IMAGE + SharedPref.getSharedPref(
//                        getApplication().getApplicationContext()).readInt(Constants.preferenceKey.IMAGE_INDEX),
//                Constants.drawables.AVATAR_DRAWABLE_DIRECTORY, getApplication().getApplicationContext().getPackageName());
//    }
}
