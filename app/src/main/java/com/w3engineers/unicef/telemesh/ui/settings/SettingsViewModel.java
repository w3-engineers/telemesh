package com.w3engineers.unicef.telemesh.ui.settings;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.appshare.application.ui.InAppShareControl;
import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.logging.Handler;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class SettingsViewModel extends BaseRxAndroidViewModel implements /*NetworkConfigureUtil.NetworkCallback*/ InAppShareControl.AppShareCallback {


    public SettingsViewModel(@NonNull Application application) {
        super(application);
    }

    /*@Nullable
    public String SSID_Name = null;
    @Nullable
    public String wifiInfo = null;*/

//    @NonNull
//    public MutableLiveData<Bitmap> bitmapMutableLiveData = new MutableLiveData<>();


    public boolean getCheckedStatus() {
        return SharedPref.getSharedPref(getApplication().getApplicationContext())
                .readBoolean(Constants.preferenceKey.IS_NOTIFICATION_ENABLED);
    }

    public void onCheckedChanged(boolean checked) {
        SharedPref.getSharedPref(getApplication().getApplicationContext())
                .write(Constants.preferenceKey.IS_NOTIFICATION_ENABLED, checked);
    }

    @NonNull
    public String getAppLanguage() {

        String language = SharedPref.getSharedPref(App.getContext()).read(Constants.preferenceKey.APP_LANGUAGE_DISPLAY);
        return !language.equals("") ? language : App.getContext().getString(R.string.demo_language);
    }

    public void setLocale(@NonNull String lang, @Nullable String landDisplay) {

        SharedPref.getSharedPref(getApplication().getApplicationContext()).write(Constants.preferenceKey.APP_LANGUAGE, lang);
        SharedPref.getSharedPref(getApplication().getApplicationContext()).write(Constants.preferenceKey.APP_LANGUAGE_DISPLAY, landDisplay);

        LanguageUtil.setAppLanguage(getApplication().getApplicationContext(), lang);
    }

    public void startInAppShareProcess() {

//        RmDataHelper.getInstance().stopRmService();

        InAppShareControl.getInstance().startInAppShareProcess(getApplication().getApplicationContext(), this);
    }

    @Override
    public void closeRmService() {
        RmDataHelper.getInstance().stopRmService();
    }

    @Override
    public void successShared() {
        HandlerUtil.postBackground(() -> {
            String date = TimeUtil.getDateString(System.currentTimeMillis());
            String myId = SharedPref.getSharedPref(App.getContext()).read(Constants.preferenceKey.MY_USER_ID);
            boolean isExist = AppShareCountDataService.getInstance().isCountExist(myId, date);
            if (isExist) {
                AppShareCountDataService.getInstance().updateCount(myId, date);
            } else {
                AppShareCountEntity entity = new AppShareCountEntity();
                entity.setDate(date);
                entity.setCount(1);
                entity.setUserId(myId);
                AppShareCountDataService.getInstance().insertAppShareCount(entity);
            }
        });
    }

    @Override
    public void closeInAppShare() {
        HandlerUtil.postBackground(()-> ServiceLocator.getInstance().resetMesh(), 5000);
//        ServiceLocator.getInstance().resetMesh();
    }
}
