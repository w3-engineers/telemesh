package com.w3engineers.unicef.telemesh.ui.splashscreen;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.w3engineers.mesh.application.data.local.db.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RmDataHelper;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.db.DataSource;
import com.w3engineers.unicef.telemesh.data.local.dbsource.Source;
import com.w3engineers.unicef.util.base.ui.BaseRxViewModel;
import com.w3engineers.unicef.util.helper.CommonUtil;

import io.reactivex.schedulers.Schedulers;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class SplashViewModel extends BaseRxViewModel {

    private MutableLiveData<Boolean> isUserRegistered = new MutableLiveData<>();
    private DataSource dataSource;
    private MutableLiveData<Boolean> walletPrepareLiveData;
    public SplashViewModel(@NonNull Application application) {

        dataSource = Source.getDbSource();
        walletPrepareLiveData = new MutableLiveData<>();

    }

    @NonNull
    public MutableLiveData<Boolean> getIsUserRegistered() {
        return isUserRegistered;
    }

    public void getUserRegistrationStatus() {

        RmDataHelper.getInstance().resetUserToOfflineBasedOnService();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> isUserRegistered.postValue(SharedPref
                        .readBoolean(Constants.preferenceKey.IS_USER_REGISTERED)),
                Constants.DefaultValue.DELAY_INTERVAL);
    }

    @NonNull
    public String getAppVersion() {
        Context context = TeleMeshApplication.getContext().getApplicationContext();
        return context.getString(R.string.app_name) + " " + context.getString(R.string.app_version) + BuildConfig.VERSION_NAME;
    }


    public void initWalletPreparationCallback() {
        getCompositeDisposable().add(dataSource.getWalletPrepared()
                .subscribeOn(Schedulers.newThread())
                .subscribe(aBoolean -> {
                    walletPrepareLiveData.postValue(aBoolean);
                }, Throwable::printStackTrace));
    }

    public MutableLiveData<Boolean> getWalletPrepareLiveData() {
        return walletPrepareLiveData;
    }
}