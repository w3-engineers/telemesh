package com.w3engineers.appshare.util.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.w3engineers.appshare.R;
import com.w3engineers.appshare.application.ui.InAppShareControl;
import com.w3engineers.appshare.util.lib.InstantServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

public class InAppShareUtil {

    private static InAppShareUtil inAppShareUtil = new InAppShareUtil();
    private Random random = new Random();
    private InAppShareCallback inAppShareCallback = null;

    private BehaviorSubject<Integer> downloadedStateCount = BehaviorSubject.create();

    @NonNull
    public String serverAddress = "";
    @Nullable
    public Bitmap serverAddressBitmap = null, wifiAddressBitmap = null;

    @NonNull
    public static InAppShareUtil getInstance() {
        return inAppShareUtil;
    }

    public void setInAppShareCallback(@Nullable InAppShareCallback inAppShareCallback) {
        this.inAppShareCallback = inAppShareCallback;
    }

    public interface InAppShareCallback {
        void inAppShareAssets(boolean isServerReady);
    }

    /**
     * Prepared a Qr bitmap and its resolution is 150 x 150
     * @param Value - Qr code string
     * @return - Qr bitmap
     */
    @Nullable
    public synchronized Bitmap getQrBitmap(@NonNull String Value) {
        Bitmap bitmap = null;

        try {
            Context context = InAppShareControl.getInstance().getAppShareContext();

            BitMatrix bitMatrix = new MultiFormatWriter().encode(Value, BarcodeFormat.QR_CODE,
                    150, 150, null);

            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;

                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            context.getResources().getColor(R.color.black) :
                            context.getResources().getColor(R.color.white);
                }
            }

            bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, 150, 0, 0, bitMatrixWidth, bitMatrixHeight);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * This api is responsible for app share server initialization
     * random port generating and preparing a server url address
     * @return - server address
     */
    @Nullable
    public Bitmap serverInit() {

        String myAddress = "192.168.49.1";
        int httpPort = random.nextInt(9000) + 1000;

        serverAddress = "http://" + myAddress + ":" + httpPort;

        String backApkPath = getBackUpApkPath();

        String backServiceApkPath = getServiceApkPath();

        if (backApkPath != null && backServiceApkPath != null) {
            InstantServer.getInstance().setPort(httpPort).setFilePath(backApkPath).setServiceFilePath(backServiceApkPath).startServer();

            wifiAddressBitmap = getQrBitmap("WIFI:S:" + NetworkConfigureUtil.getInstance().getNetworkName() + ";T:WPA;P:" + NetworkConfigureUtil.getInstance().getNetworkPass() + ";");

            serverAddressBitmap = getQrBitmap(serverAddress);

            if (inAppShareCallback != null) {
                inAppShareCallback.inAppShareAssets(true);
            }
        } else {
            NetworkConfigureUtil.getInstance().triggerNetworkFailed("Storage problem occurred");
        }

        return serverAddressBitmap;
    }

    /**
     * Responsible for getting telemesh apk path
     * At first it will check apk is exist or not
     * If exist then check the same version code
     * If all conditions will not satisfy then create a apk and store it in sd card
     * @return - Get the back up apk path
     */
    @Nullable
    public String getBackUpApkPath() {
        Context context = InAppShareControl.getInstance().getAppShareContext();

        String myApplicationName = context.getResources().getString(R.string.app_name);
        String myApplicationPackageName = context.getPackageName();

        return backupApkAndGetPath(context, myApplicationName, myApplicationPackageName);
    }

    @Nullable
    public String getServiceApkPath() {
        Context context = InAppShareControl.getInstance().getAppShareContext();

        String myServiceAppPackageName = "com.w3engineers.meshservice";
        String myServiceAppName = "TeleService";

        return backupApkAndGetPath(context, myServiceAppName, myServiceAppPackageName);
    }

    /**
     * get application info and preparing a apk and save it in local storage
     * @param context - Need an application context for getting package name
     * @return - saved apk path
     */
    @Nullable
    public String backupApkAndGetPath(@NonNull Context context, String appName, String packageName) {


        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        String backupFolder = ".backup";

        for (Object object : pkgAppsList) {

            ResolveInfo resolveInfo = (ResolveInfo) object;
            File appFile = new File(resolveInfo.activityInfo.applicationInfo.publicSourceDir);

            try {

                String file_name = resolveInfo.loadLabel(context.getPackageManager()).toString();

                if (file_name.equalsIgnoreCase(appName) &&
                        appFile.toString().contains(packageName)) {

                    File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                            context.getString(R.string.app_name));
                    file.mkdirs();
                    // Preparing a backup apk folder and it is hidden
                    File backUpFolder = new File(file.getAbsolutePath() + "/" + backupFolder);
                    backUpFolder.mkdirs();
                    backUpFolder = new File(backUpFolder.getPath() + "/" + file_name + ".apk");
                    backUpFolder.createNewFile();

                    InputStream in = new FileInputStream(appFile);
                    OutputStream out = new FileOutputStream(backUpFolder);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    return backUpFolder.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public boolean isInAppShareEnable() {
        return !TextUtils.isEmpty(serverAddress) && serverAddressBitmap != null && wifiAddressBitmap != null;
    }

    /**
     * Reset all properties when in app share process is completed
     */
    public void resetAppShareServerProperties() {
        inAppShareCallback = null;
        serverAddress = "";
        serverAddressBitmap = null;
        wifiAddressBitmap = null;
    }

    public void setDownloadCount(int pendingDownloadCount) {
        downloadedStateCount.onNext(pendingDownloadCount);
    }

    public Flowable<Integer> getDownloadStateCount() {
        return downloadedStateCount.toFlowable(BackpressureStrategy.LATEST);
    }
}
