package com.w3engineers.unicef.telemesh.ui.settings;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.util.helper.InAppShareUtil;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.NetworkConfigureUtil;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class SettingsViewModel extends AndroidViewModel {


    public SettingsViewModel(@NonNull Application application) {
        super(application);
    }

    public String SSID_Name = null;
    public String wifiInfo = null;

    public MutableLiveData<Bitmap> bitmapMutableLiveData = new MutableLiveData<>();


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

    /**
     * Start background process for starting in app share
     * At first it responsible to configure a network for sharing app
     * Then start in app share server
     */
    public void startInAppShareProcess() {
        this.SSID_Name = null;

        getCompositeDisposable().add(getSingleRouterConfiguration()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {}, Throwable::printStackTrace));
    }

    /**
     * This api used for calling startRouterConfigureProcess.
     * In disposable can't operate directly any api's from another class
     * @return - Get the completion state
     */
    @NonNull
    private Boolean getRouterConfigure() {
        return NetworkConfigureUtil.getInstance()
                .setNetworkCallback(SettingsViewModel.this).startRouterConfigureProcess();
    }

    /**
     * When a network is established
     * we get this callback with network name
     * @param SSID - Configured network name
     */
    @Override
    public void networkName(@NonNull String SSID) {

        Context context = getApplication().getApplicationContext();

        this.SSID_Name = SSID;

        String wifiName = SSID_Name;
        String wifiPass = NetworkConfigureUtil.getInstance().SSID_Key;

        wifiInfo = String.format(context.getString(R.string.hotspot_id_pass), wifiName, wifiPass);
        String QrText = "WIFI:T:WPA;P:\"" + wifiPass + "\";S:" + wifiName + ";";

        bitmapMutableLiveData.postValue(InAppShareUtil.getInstance().getQrBitmap(QrText));

        initServerProcess();
    }

    /**
     * When network is ready then we start our In-App share server
     */
    private void initServerProcess() {
        getCompositeDisposable().add(serverInitSingleCallable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> InAppShareUtil.getInstance().urlQrGenerator(address),
                        Throwable::printStackTrace));
    }

    /**
     * Preparing a single instance for calling in app share server
     * @return - Get Rx single object
     */
    private Single<String> serverInitSingleCallable() {
        return Single.fromCallable(this::serverInit);
    }

    /**
     * Preparing a single instance for configuring router configuration
     * @return - Get Rx single object
     */
    private Single<Boolean> getSingleRouterConfiguration() {
        return Single.fromCallable(this::getRouterConfigure);
    }

    /**
     * Trigger in app share server to start
     * @return - Get the server url
     */
    @Nullable
    private String serverInit() {
        return InAppShareUtil.getInstance().serverInit();
    }

    /**
     * Reset RM data source instance null for configuring the RM service restart
     * and restart the RM service
     */
    public void resetRM() {
        if (NetworkConfigureUtil.getInstance().isRmOff()) {
            NetworkConfigureUtil.getInstance().setRmOff(false);
            ServiceLocator.getInstance().resetRmDataSourceInstance();
            ServiceLocator.getInstance().restartRmService();
        }
    }
}
