package com.w3engineers.appshare.application.ui;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.w3engineers.appshare.util.helper.InAppShareUtil;
import com.w3engineers.appshare.util.helper.NetworkConfigureUtil;
import com.w3engineers.appshare.util.lib.InstantServer;
import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

public class InAppShareViewModel extends BaseRxAndroidViewModel implements InAppShareUtil.InAppShareCallback,
        NetworkConfigureUtil.NetworkCallback {

    @Nullable
    public String SSID_Name = null;
    @Nullable
    public String wifiInfo = null;

    public InAppShareViewModel(@NonNull Application application) {
        super(application);
        InAppShareUtil.getInstance().setInAppShareCallback(this);
    }

    @NonNull
    public MutableLiveData<Boolean> appShareStateLiveData = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Stop the InApp share server
     * when In app share process is fully done
     */
    public void stopServerProcess() {
        InstantServer.getInstance().stopServer();
    }

    /**
     * Reset RM data source instance null
     * for configuring the RM service restart
     */
    public void resetRM() {
        if (NetworkConfigureUtil.getInstance().isRmOff()) {
            NetworkConfigureUtil.getInstance().setRmOff(false);

            InAppShareControl.AppShareCallback appShareCallback = InAppShareControl.getInstance().getAppShareCallback();
            if (appShareCallback != null) {
                appShareCallback.closeInAppShare();
            }

//            ServiceLocator.getInstance().resetRmDataSourceInstance();
        }
    }

    public void resetAllInfo() {
        NetworkConfigureUtil.getInstance().resetNetworkConfigureProperties();
        InAppShareUtil.getInstance().resetAppShareServerProperties();
    }

    public void checkInAppShareState() {
        appShareStateLiveData.postValue(InAppShareUtil.getInstance().isInAppShareEnable());
    }

    @Override
    public void inAppShareAssets(boolean isServerReady) {
        appShareStateLiveData.postValue(isServerReady);
    }

    /**
     * Start background process for starting in app share
     * At first it responsible to configure a network for sharing app
     * Then start in app share server
     */
    public void startInAppShareProcess() {
        this.SSID_Name = null;

        compositeDisposable.add(getSingleRouterConfiguration()
                .subscribeOn(Schedulers.newThread())
                .subscribe(aBoolean -> {}, Throwable::printStackTrace));
    }

    /**
     * Preparing a single instance for configuring router configuration
     * @return - Get Rx single object
     */
    private Single<Boolean> getSingleRouterConfiguration() {
        return Single.fromCallable(this::getRouterConfigure);
    }

    /**
     * This api used for calling startRouterConfigureProcess.
     * In disposable can't operate directly any api's from another class
     * @return - Get the completion state
     */
    @NonNull
    private Boolean getRouterConfigure() {
        return NetworkConfigureUtil.getInstance()
                .setNetworkCallback(this).startRouterConfigureProcess();
    }

    /**
     * When a network is established
     * we get this callback with network name
     */
    @Override
    public void networkName() {
        initServerProcess();
    }

    /**
     * When network is ready then we start our In-App share server
     */
    private void initServerProcess() {
        getCompositeDisposable().add(serverInitSingleCallable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qrCode -> {}, Throwable::printStackTrace));
    }

    /**
     * Preparing a single instance for calling in app share server
     * @return - Get Rx single object
     */
    private Single<Bitmap> serverInitSingleCallable() {
        return Single.fromCallable(this::serverInit);
    }

    /**
     * Trigger in app share server to start
     * @return - Get the server url
     */
    @Nullable
    private Bitmap serverInit() {
        return InAppShareUtil.getInstance().serverInit();
    }
}
