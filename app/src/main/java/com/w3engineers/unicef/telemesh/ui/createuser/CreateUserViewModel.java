package com.w3engineers.unicef.telemesh.ui.createuser;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class CreateUserViewModel extends AndroidViewModel {

    private int imageIndex = CreateUserActivity.INITIAL_IMAGE_INDEX;

    public CreateUserViewModel(@NonNull Application application) {
        super(application);
    }

    int getImageIndex() {
        return imageIndex;
    }

    void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    boolean storeData(@Nullable String firstName, @Nullable String lastName, @NonNull String myUserId) {

        // Store name and image on PrefManager
        SharedPref sharedPref = SharedPref.getSharedPref(getApplication().getApplicationContext());

        sharedPref.write(Constants.preferenceKey.FIRST_NAME, firstName);
        sharedPref.write(Constants.preferenceKey.LAST_NAME, lastName);
        sharedPref.write(Constants.preferenceKey.IMAGE_INDEX, imageIndex);
        sharedPref.write(Constants.preferenceKey.MY_USER_ID, myUserId);
        sharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, true);
        return true;
    }

    boolean isNameValid(@Nullable String name) {
        return !TextUtils.isEmpty(name) && name != null &&
                name.length() >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT
                && name.length() <= Constants.DefaultValue.MAXIMUM_TEXT_LIMIT;
    }
}
