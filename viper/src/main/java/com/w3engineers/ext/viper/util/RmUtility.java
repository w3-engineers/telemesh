package com.w3engineers.ext.viper.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import com.w3engineers.ext.viper.R;
import timber.log.Timber;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class RmUtility {

    /**
     * Retrieve RM service and App layer service
     * @param context
     * @return
     */
    public static List<Integer> getMyProcessIdList(Context context) {

        List<Integer> myProcessIdList = null;

        if(context != null) {
            ActivityManager manager
                    = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            if (manager != null) {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = manager.getRunningAppProcesses();

                myProcessIdList = new ArrayList<>(2);

                for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {

                    if (processInfo != null) {

                        Timber.d(processInfo.processName);

                        // Actually we need to kill only local scope remote service rather the native
                        // mesh service. Although keeping the scope and commenting code if by anyhow
                        // any particular project is in need of this unusual requirement
                        if (//processInfo.processName.contains(context.getString(R.string.rm_process_name)) ||
                                processInfo.processName.contains(context.getString(R.string.base_rm_process_name))) {
                            myProcessIdList.add(processInfo.pid);
                        }
                    }
                }
            }
        }

        return myProcessIdList;
    }

}
