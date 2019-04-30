package com.w3engineers.unicef.telemesh.ui.settings;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.appshare.application.ui.InAppShareControl;
import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.util.helper.LanguageUtil;

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
        InAppShareControl.getInstance().startInAppShareProcess(getApplication().getApplicationContext(), this);
    }

    @Override
    public void closeRmService() {
        RmDataHelper.getInstance().stopRmService();
    }

    @Override
    public void closeInAppShare() {
        ServiceLocator.getInstance().resetRmDataSourceInstance();
    }

    /**
     * Start background process for starting in app share
     * At first it responsible to configure a network for sharing app
     * Then start in app share server
     *//*
    public void startInAppShareProcess() {
        this.SSID_Name = null;

        getCompositeDisposable().add(getSingleRouterConfiguration()
                .subscribeOn(Schedulers.newThread())
                .subscribe(aBoolean -> {}, Throwable::printStackTrace));
    }

    *//**
     * Preparing a single instance for configuring router configuration
     * @return - Get Rx single object
     *//*
    private Single<Boolean> getSingleRouterConfiguration() {
        return Single.fromCallable(this::getRouterConfigure);
    }

    *//**
     * This api used for calling startRouterConfigureProcess.
     * In disposable can't operate directly any api's from another class
     * @return - Get the completion state
     *//*
    @NonNull
    private Boolean getRouterConfigure() {
        return NetworkConfigureUtil.getInstance()
                .setNetworkCallback(SettingsViewModel.this).startRouterConfigureProcess();
    }

    *//**
     * When a network is established
     * we get this callback with network name
     *//*
    @Override
    public void networkName() {
        initServerProcess();
    }

    *//**
     * When network is ready then we start our In-App share server
     *//*
    private void initServerProcess() {
        getCompositeDisposable().add(serverInitSingleCallable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qrCode -> {}, Throwable::printStackTrace));
    }

    *//**
     * Preparing a single instance for calling in app share server
     * @return - Get Rx single object
     *//*
    private Single<Bitmap> serverInitSingleCallable() {
        return Single.fromCallable(this::serverInit);
    }

    *//**
     * Trigger in app share server to start
     * @return - Get the server url
     *//*
    @Nullable
    private Bitmap serverInit() {
        return InAppShareUtil.getInstance().serverInit();
    }*/
}
