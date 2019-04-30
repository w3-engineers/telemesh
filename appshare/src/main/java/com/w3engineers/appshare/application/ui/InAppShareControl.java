package com.w3engineers.appshare.application.ui;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.content.Context;
import android.content.Intent;

public class InAppShareControl {

    private static InAppShareControl inAppShareControl = new InAppShareControl();
    private Context context;
    private AppShareCallback appShareCallback;

    public static InAppShareControl getInstance() {
        return inAppShareControl;
    }

    public interface AppShareCallback {
        void closeRmService();
        void closeInAppShare();
    }

    public void startInAppShareProcess(Context context, AppShareCallback appShareCallback) {
        this.context = context;
        setAppShareCallback(appShareCallback);
        Intent intent = new Intent(context, InAppShareActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public AppShareCallback getAppShareCallback() {
        return appShareCallback;
    }

    public void setAppShareCallback(AppShareCallback appShareCallback) {
        this.appShareCallback = appShareCallback;
    }

    public Context getAppShareContext() {
        return context;
    }
}
