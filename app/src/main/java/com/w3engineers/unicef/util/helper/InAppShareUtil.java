package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.w3engineers.ext.strom.App;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.ui.inappshare.InstantServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [13-Mar-2019 at 6:33 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [13-Mar-2019 at 6:33 PM].
 * --> <Second Editor> on [13-Mar-2019 at 6:33 PM].
 * Reviewed by :
 * --> <First Reviewer> on [13-Mar-2019 at 6:33 PM].
 * --> <Second Reviewer> on [13-Mar-2019 at 6:33 PM].
 * ============================================================================
 **/
public class InAppShareUtil {

    private static InAppShareUtil inAppShareUtil = new InAppShareUtil();

    private Bitmap urlQrBitmap = null;
    private String serverAddress = "";
    private Random random = new Random();

    @NonNull
    public static InAppShareUtil getInstance() {
        return inAppShareUtil;
    }

    /**
     * Get In app share server url or code bitmap
     * @return - QR code bitmap
     */
    @Nullable
    public Bitmap getUrlQR() {
        return urlQrBitmap;
    }

    /**
     * Get in app share server address
     * @return - server url link
     */
    @NonNull
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Get in app share url address and prepared a bitmap
     * @param Value - In app share qr code url
     */
    public void urlQrGenerator(@NonNull String Value) {
        urlQrBitmap = getQrBitmap(Value);
    }

    /**
     * Prepared a Qr bitmap and its resolution is 150 x 150
     * @param Value - Qr code string
     * @return - Qr bitmap
     */
    @Nullable
    public Bitmap getQrBitmap(@NonNull String Value) {
        Bitmap bitmap = null;

        try {
            Context context = App.getContext();

            BitMatrix bitMatrix = new MultiFormatWriter().encode(Value, BarcodeFormat.DATA_MATRIX.QR_CODE,
                    150, 150, null
            );

            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;

                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            context.getResources().getColor(R.color.colorPrimaryDark) :
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
    public String serverInit() {

        String myAddress = getMyIpAddress();
        int httpPort = random.nextInt(9000) + 1000;

        serverAddress = "http://" + myAddress + ":" + httpPort;

        String backApkPath = getBackUpApkPath();

        InstantServer.getInstance().setPort(httpPort).setFilePath(backApkPath);

        return serverAddress;
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
        Context context = App.getContext();

        try {
            String myApplicationName = context.getResources().getString(R.string.app_name) + ".apk";
            String backupFolder = ".backup";

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    context.getString(R.string.app_name) + "/" + backupFolder + "/" + myApplicationName);

            if (file.exists()) {

                PackageInfo myPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(), 0);
                int versionCode = packageInfo.versionCode;

                if (versionCode != myPackageInfo.versionCode) {
                    return backupApkAndGetPath(context);
                } else {
                    return file.getAbsolutePath();
                }
            } else {
                return backupApkAndGetPath(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backupApkAndGetPath(context);
    }

    /**
     * get application info and preparing a apk and save it in local storage
     * @param context - Need an application context for getting package name
     * @return - saved apk path
     */
    @Nullable
    public String backupApkAndGetPath(@NonNull Context context) {


        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        String myApplicationPackageName = context.getPackageName();
        String myApplicationName = context.getResources().getString(R.string.app_name);
        String backupFolder = ".backup";

        for (Object object : pkgAppsList) {

            ResolveInfo resolveInfo = (ResolveInfo) object;
            File appFile = new File(resolveInfo.activityInfo.applicationInfo.publicSourceDir);

            try {

                String file_name = resolveInfo.loadLabel(context.getPackageManager()).toString();

                if (file_name.equalsIgnoreCase(myApplicationName) &&
                        appFile.toString().contains(myApplicationPackageName)) {

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

    /**
     * Responsible for this api is getting my device local ip address
     * @return - my local ip address
     */
    @Nullable
    private String getMyIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                if (networkInterface.isLoopback()) {
                    continue;
                }
                if (networkInterface.isVirtual()) {
                    continue;
                }
                if (!networkInterface.isUp()) {
                    continue;
                }
                if (networkInterface.isPointToPoint()) {
                    continue;
                }
                if (networkInterface.getHardwareAddress() == null) {
                    continue;
                }
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress.getAddress().length == 4) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
