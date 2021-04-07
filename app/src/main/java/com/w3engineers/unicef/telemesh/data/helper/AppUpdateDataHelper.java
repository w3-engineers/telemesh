package com.w3engineers.unicef.telemesh.data.helper;

/*
 * ============================================================================
 * Copyright (C) 2021 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import com.google.gson.Gson;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.mesh.util.NetworkMonitor;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.AppInstaller;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdate;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.InAppUpdateModel;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountDataService;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.AppShareCountEntity;
import com.w3engineers.unicef.telemesh.data.local.appsharecount.ShareCountModel;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AppUpdateDataHelper {
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static AppUpdateDataHelper appUpdateDataHelper;

    public static AppUpdateDataHelper getInstance() {
        if (appUpdateDataHelper == null) {
            appUpdateDataHelper = new AppUpdateDataHelper();
            return appUpdateDataHelper;
        }
        return appUpdateDataHelper;
    }

    public void versionCrossMatching(byte[] rawData, String userId, boolean isAckSuccess) {
        if (isAckSuccess) return;

        String appVersionData = new String(rawData);
        Timber.tag("InAppUpdateTest").d("version rcv: " + appVersionData + " userId: " + userId);
        InAppUpdateModel versionModel = new Gson().fromJson(appVersionData, InAppUpdateModel.class);

        InAppUpdateModel myVersionModel = InAppUpdate.getInstance(TeleMeshApplication.getContext()).getAppVersion();

        InAppUpdate instance = InAppUpdate.getInstance(TeleMeshApplication.getContext());

        String myServerLink = instance.getMyLocalServerLink();
        Timber.tag("InAppUpdateTest").d("My version Code: %s", myVersionModel.getVersionCode());
        if (myVersionModel.getVersionCode() > versionModel.getVersionCode()) {

            if (myServerLink != null) {
                // start my server
                if (!instance.isServerRunning()) {
                    instance.prepareLocalServer();
                }

                InAppUpdateModel model = new InAppUpdateModel();
                model.setUpdateLink(myServerLink);
                String data = new Gson().toJson(model);
                RmDataHelper.getInstance().dataSend(data.getBytes(), Constants.DataType.SERVER_LINK, userId, false);

                Timber.tag("InAppUpdateTest").d("My version is Big: ");
            }
        } else if (versionModel.getVersionCode() > myVersionModel.getVersionCode()) {
            if (versionModel.getUpdateType() == Constants.AppUpdateType.BLOCKER) {
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().openAppBlocker(versionModel.getVersionName());
                }
            }
        }
    }

    public void startAppUpdate(byte[] rawData, boolean isAckSuccess, String userId) {
        if (isAckSuccess) return;

        int userActiveStatus = MeshDataSource.getRmDataSource().getUserActiveStatus(userId);

        int userConnectivityStatus = MeshUserDataHelper.getInstance().getActiveStatus(userActiveStatus);

        if (userConnectivityStatus != Constants.UserStatus.WIFI_ONLINE) {
            return;
        }

        SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());
        if (sharedPref.readBoolean(Constants.preferenceKey.ASK_ME_LATER)) {
            long saveTime = sharedPref.readLong(Constants.preferenceKey.ASK_ME_LATER_TIME);
            long days = (System.currentTimeMillis() - saveTime) / (24 * 60 * 60 * 1000);

            if (days <= 2) return;
        }

        String appVersionData = new String(rawData);
        InAppUpdateModel versionModel = new Gson().fromJson(appVersionData, InAppUpdateModel.class);

        //AppInstaller.downloadApkFile(versionModel.getUpdateLink(), MainActivity.getInstance());

        if (!InAppUpdate.getInstance(TeleMeshApplication.getContext()).isAppUpdating()) {
            //InAppUpdate.getInstance(TeleMeshApplication.getContext()).setAppUpdateProcess(true);
            if (MainActivity.getInstance() == null) return;
            InAppUpdate.getInstance(TeleMeshApplication.getContext()).checkForUpdate(MainActivity.getInstance(), versionModel.getUpdateLink());
        }
    }

    public void saveAppShareCount(byte[] rawData, boolean isAckSuccess) {
        try {
            String shareCountString = new String(rawData);

            ShareCountModel shareCountModel = new Gson().fromJson(shareCountString, ShareCountModel.class);
            AppShareCountEntity entity = new AppShareCountEntity().toAppShareCountEntity(shareCountModel);

            if (!isAckSuccess) {
                compositeDisposable.add(Single.fromCallable(() -> AppShareCountDataService.getInstance()
                        .insertAppShareCount(entity))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(longResult -> {
                            if (longResult > 1) {
//                                Timber.tag("AppShareCount").d("Data saved");
                            }
                        }, Throwable::printStackTrace));


            } else {
                compositeDisposable.add(Single.fromCallable(() -> AppShareCountDataService.getInstance()
                        .updateSentShareCount(entity.getUserId(), entity.getDate()))
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(longResult -> {
                            if (longResult > 1) {
//                                Timber.tag("AppShareCount").d("Data Deleted");
                            }
                        }, Throwable::printStackTrace));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void appUpdateFromOtherServer(int type, String normalUpdateJson) {

        // check app update for internet;

        if (type == Constants.AppUpdateType.BLOCKER) {
            if (NetworkMonitor.isOnline()) {
                InAppUpdate.getInstance(MainActivity.getInstance()).setAppUpdateProcess(true);

                AppInstaller.downloadApkFile(AppCredentials.getInstance().getFileRepoLink(), MainActivity.getInstance(), NetworkMonitor.getNetwork());
            }

        } else {
            HandlerUtil.postForeground(() -> {
                if (!InAppUpdate.getInstance(TeleMeshApplication.getContext()).isAppUpdating()) {
                    //InAppUpdate.getInstance(TeleMeshApplication.getContext()).setAppUpdateProcess(true);
                    if (MainActivity.getInstance() == null) return;

                    SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());
                    if (sharedPref.readBoolean(Constants.preferenceKey.ASK_ME_LATER)) {
                        long saveTime = sharedPref.readLong(Constants.preferenceKey.ASK_ME_LATER_TIME);
                        long dif = System.currentTimeMillis() - saveTime;
                        long days = dif / (24 * 60 * 60 * 1000);

                        if (days <= 2) return;
                    }

                    // We can show the dialog directly by creating a json file

                    InAppUpdate.getInstance(MainActivity.getInstance()).showAppInstallDialog(normalUpdateJson, MainActivity.getInstance());

                    // InAppUpdate.getInstance(TeleMeshApplication.getContext()).checkForUpdate(MainActivity.getInstance(), InAppUpdate.LIVE_JSON_URL);
                }
            }, TimeUnit.SECONDS.toMillis(5));
        }

    }

    public void destroy() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable.dispose();
        }
    }

}
