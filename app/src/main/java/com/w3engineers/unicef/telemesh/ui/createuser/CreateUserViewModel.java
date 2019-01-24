package com.w3engineers.unicef.telemesh.ui.createuser;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [17-Sep-2018 at 3:54 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [17-Sep-2018 at 3:54 PM].
 * * --> <Second Editor> on [17-Sep-2018 at 3:54 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [17-Sep-2018 at 3:54 PM].
 * * --> <Second Reviewer> on [17-Sep-2018 at 3:54 PM].
 * * ============================================================================
 **/
public class CreateUserViewModel extends AndroidViewModel {

    public int imageIndex = CreateUserActivity.INITIAL_IMAGE_INDEX;

    public CreateUserViewModel(@NonNull Application application) {
        super(application);
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public boolean storeData(String firstName, String lastName) {

        // Store name and image on PrefManager
        SharedPref sharedPref = SharedPref.getSharedPref(getApplication().getApplicationContext());

        sharedPref.write(Constants.preferenceKey.FIRST_NAME, firstName);
        sharedPref.write(Constants.preferenceKey.LAST_NAME, lastName);
        sharedPref.write(Constants.preferenceKey.IMAGE_INDEX, imageIndex);
        sharedPref.write(Constants.preferenceKey.IS_USER_REGISTERED, true);
        return true;
    }

    public boolean isNameValid(String name) {
        return !TextUtils.isEmpty(name) &&
                name.length() >= Constants.DefaultValue.MINIMUM_TEXT_LIMIT
                && name.length() <= Constants.DefaultValue.MAXIMUM_TEXT_LIMIT;
    }
}
