package com.w3engineers.unicef.telemesh.ui.splashscreen;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class SplashViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> isUserRegistered = new MutableLiveData<>();

    public SplashViewModel(@NonNull Application application) {
        super(application);
    }

    @NonNull
    public MutableLiveData<Boolean> getIsUserRegistered() {
        return isUserRegistered;
    }

    public void getUserRegistrationStatus() {


        RmDataHelper.getInstance().resetUserToOfflineBasedOnService();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> isUserRegistered.postValue(SharedPref.getSharedPref(getApplication()
                .getApplicationContext()).readBoolean(Constants.preferenceKey.IS_USER_REGISTERED)),
                Constants.DefaultValue.DELAY_INTERVAL);
    }

    @NonNull
    public String getAppVersion() {
        Context context = getApplication().getApplicationContext();
        return context.getString(R.string.app_name) + " " + context.getString(R.string.app_version) + BuildConfig.VERSION_NAME;
    }
}