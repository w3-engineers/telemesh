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

import com.w3engineers.appshare.util.lib.InAppShareWebController;

public class InAppShareControl {


    private static InAppShareControl inAppShareControl = new InAppShareControl();
    private Context context;
    private AppShareCallback appShareCallback;

    public static InAppShareControl getInstance() {
        return inAppShareControl;
    }

    public interface AppShareCallback {
        void closeRmService();
        void successShared();
        void closeInAppShare();
    }

    public void startInAppShareProcess(Context context, AppShareCallback appShareCallback) {
        this.context = context;
        setAppShareCallback(appShareCallback);
        InAppShareWebController.getInAppShareWebController().initContext(context);
        Intent intent = new Intent(context, InAppShareActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
