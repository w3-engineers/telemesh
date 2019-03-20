package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
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

    public static InAppShareUtil getInstance() {
        return inAppShareUtil;
    }

    public Bitmap getUrlQR() {
        return urlQrBitmap;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void qrGenerator(String Value) {
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
            urlQrBitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            urlQrBitmap.setPixels(pixels, 0, 150, 0, 0, bitMatrixWidth, bitMatrixHeight);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public String serverInit() {

        String myAddress = getMyIpAddress();
        int httpPort = random.nextInt(9000) + 1000;

        serverAddress = "http://" + myAddress + ":" + httpPort;

        String backApkPath = backupApkAndGetPath();

        InstantServer.getInstance().setPort(httpPort).setFilePath(backApkPath);

        return serverAddress;
    }

    @Nullable
    public String backupApkAndGetPath() {

        Context context = App.getContext();

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
