package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class StorageUtil {

    private static long UNIT = 1048576;

   /* public static boolean hasStorage(Context context) {
        IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        boolean hasLowStorage = context.registerReceiver(null, lowstorageFilter) != null;

        return hasLowStorage;
    }*/

    /**
     * The return storage define the available storage in mobile and it show as Megabyte
     *
     * @return Long
     */
    public static long getFreeMemory() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long free = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            free = (statFs.getBlockCountLong() * statFs.getBlockSizeLong()) / UNIT;
        } else {
            free = (statFs.getAvailableBlocks() * statFs.getBlockSize()) / UNIT;
        }

        Log.d("FreeSpace", " Storage free space in MB: " + free);

        return free;
    }
}
