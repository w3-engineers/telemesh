package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.w3engineers.unicef.telemesh.R;

public class CustomDialogUtil {

    private static Dialog dialog;

    /**
     * Common progress dialog ... initiates with this
     */
    public static void showProgressDialog(Context context) {
        if (dialog == null) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.alert_loading_progress);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public static boolean isProgressDialogRunning() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        } else return false;
    }

    /**
     * Dismiss comman progress dialog
     */
    public static void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

}
