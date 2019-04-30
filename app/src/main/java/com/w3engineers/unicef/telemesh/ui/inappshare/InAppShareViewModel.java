/*
package com.w3engineers.unicef.telemesh.ui.inappshare;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.util.helper.InAppShareUtil;
import com.w3engineers.unicef.util.helper.NetworkConfigureUtil;

*/
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *//*


public class InAppShareViewModel extends BaseRxAndroidViewModel implements InAppShareUtil.InAppShareCallback {

    public InAppShareViewModel(@NonNull Application application) {
        super(application);
        InAppShareUtil.getInstance().setInAppShareCallback(this);
    }

    @NonNull
    public MutableLiveData<Boolean> appShareStateLiveData = new MutableLiveData<>();

    */
/**
     * Stop the InApp share server
     * when In app share process is fully done
     *//*

    public void stopServerProcess() {
        InstantServer.getInstance().stopServer();
    }

    */
/**
     * Reset RM data source instance null
     * for configuring the RM service restart
     *//*

    public void resetRM() {
        if (NetworkConfigureUtil.getInstance().isRmOff()) {
            NetworkConfigureUtil.getInstance().setRmOff(false);
            ServiceLocator.getInstance().resetRmDataSourceInstance();
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
}
*/
