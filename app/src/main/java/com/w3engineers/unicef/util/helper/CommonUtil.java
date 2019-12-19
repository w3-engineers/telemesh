package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.w3engineers.mesh.util.DialogUtil;
import com.w3engineers.unicef.telemesh.R;

public class CommonUtil {

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static void showPermissionPopUp(Context mContext) {
        DialogUtil.showConfirmationDialog(mContext,
                mContext.getResources().getString(R.string.permission),
                mContext.getResources().getString(R.string.permission_for_signup),
                null,
                mContext.getString(R.string.ok),
                new DialogUtil.DialogButtonListener() {
                    @Override
                    public void onClickPositive() {
                       mContext.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    }

                    @Override
                    public void onCancel() {

                    }
                    @Override
                    public void onClickNegative() {

                    }
                });
    }

    public static void showGpsOrLocationOffPopup(Context mContext) {
        DialogUtil.showConfirmationDialog(mContext,
                mContext.getResources().getString(R.string.gps_alert),
                mContext.getResources().getString(R.string.for_better_performance),
                null,
                mContext.getString(R.string.ok),
                new DialogUtil.DialogButtonListener() {
                    @Override
                    public void onClickPositive() {
                        //mContext.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }

                    @Override
                    public void onCancel() {

                    }
                    @Override
                    public void onClickNegative() {

                    }
                });
    }

    public static void dismissDialog(){
        DialogUtil.dismissDialog();
    }
}
