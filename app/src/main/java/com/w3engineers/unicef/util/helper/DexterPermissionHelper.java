package com.w3engineers.unicef.util.helper;
 
/*
============================================================================
Copyright (C) 2020 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.app.Activity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class DexterPermissionHelper {

    private static DexterPermissionHelper dexterPermissionHelper = new DexterPermissionHelper();

    public static DexterPermissionHelper getInstance() {
        return dexterPermissionHelper;
    }

    public interface PermissionCallback {
        void onPermissionGranted();
    }

    public void requestForPermission(Activity activity, PermissionCallback permissionCallback, String... permissions) {
        Dexter.withActivity(activity)
                .withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (permissionCallback != null) {
                                permissionCallback.onPermissionGranted();
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            CommonUtil.showPermissionPopUp(activity);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> requestForPermission(activity, permissionCallback, permissions)).onSameThread().check();
    }
}
