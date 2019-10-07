package com.w3engineers.unicef.telemesh.data.helper.inappupdate;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.util.Log;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.gson.JsonObject;
import com.w3engineers.ext.viper.util.lib.mesh.MeshConfig;
import com.w3engineers.mesh.wifi.util.BnjUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.NanoHTTPD.NanoHTTPD;
import com.w3engineers.unicef.telemesh.data.helper.inappupdate.NanoHTTPD.SimpleWebServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.InetAddress;
import java.util.List;

public class InAppUpdate {

    private final String LIVE_JSON_URL = "demo.com/jsonfile"; // Configure json file that was uploaded in Main server
    private final String MAIN_JSON = "updatedJSon.json";
    private final String MAIN_APK = "updatedApk.apk";
    private final String LOCAL_IP_FIRST_PORTION = "/192";
    private static File rootFile;
    private static Context mContext;
    private static final int PORT = 8990;
    private NanoHTTPD mServer;
    private static boolean isServerRunning;
    private static boolean isAppUpdateProcessStart;

    // lock the default constructor
    private InAppUpdate() {
        // we can do initial stuff or first time stuff in here
    }

    public static InAppUpdate getInstance(Context context) {
        mContext = context;
        rootFile = mContext.getApplicationContext().getFilesDir();
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static InAppUpdate INSTANCE = new InAppUpdate();
    }

    /**
     * This method is responsible to update app from Main server
     * <p>
     * Yo
     */
    public void appUpdateFromInternet() {
        AppUpdater appUpdater = new AppUpdater(mContext) // This context may be Activity context
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON(LIVE_JSON_URL);
        appUpdater.start();
    }

    /**
     * This method is responsible for updating app from local Server
     *
     * @param localLink String (Local server link)
     */
    public void appUpdateFromLocal(String localLink) {
        if (!isAppUpdateProcessStart) {
            setAppUpdateProcess(true);
            AppUpdater appUpdater = new AppUpdater(mContext) // This context may be Activity context
                    .setDisplay(Display.DIALOG)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(localLink)
                    .setButtonDismissClickListener((dialog, which) -> setAppUpdateProcess(false));

            appUpdater.start();
        } else {
            Log.e("InAppUpdateTest", "App update process running");
        }
    }

    /**
     * This method will be called in every time when main activity open
     * Why? Because we don`t know my apk is updated or not.
     * <p>
     * One solution may have: Save the previous version in shared preference and
     * check the current app version. Then call it
     */
    public void prepareLocalServer() {
        File apkFile = backUpMainApk();
        File jsonFile = createAndUploadJsonFile();

        File f1 = new File(rootFile, MAIN_JSON);
        copyFile(jsonFile, f1);
        File f2 = new File(rootFile, MAIN_APK);
        copyFile(apkFile, f2);

        startLocalServer();
    }

    private void startLocalServer() {

        InetAddress tempAddress;

        final InetAddress myAddress = BnjUtil.getMyDeviceInetAddress(true);

        if (myAddress != null && myAddress.toString().contains(LOCAL_IP_FIRST_PORTION)) {
            tempAddress = myAddress;
        } else {
            tempAddress = BnjUtil.getLocalIpAddress();

        }

        String myIpAddress = tempAddress.getHostAddress();
        mServer = new SimpleWebServer(myIpAddress, PORT, rootFile, false);

        if (!mServer.isAlive()) {
            try {
                mServer.start();
                isServerRunning = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("InAppUpdateTest", "Server error: " + e.getMessage());
            }
        }
    }

    public boolean isServerRunning() {
        return isServerRunning;
    }

    public void stopServer() {
        if (mServer != null) {
            try {
                mServer.stop();
                isServerRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setAppUpdateProcess(boolean isUpdating) {
        isAppUpdateProcessStart = isUpdating;
    }

    public boolean isAppUpdating() {
        return isAppUpdateProcessStart;
    }

    public InAppUpdateModel getAppVersion() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            InAppUpdateModel model = new InAppUpdateModel();
            model.setVersionName(version);
            model.setVersionCode(versionCode);
            return model;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMyLocalServerLink() {
        InetAddress tempAddress;
        final InetAddress myAddress = BnjUtil.getMyDeviceInetAddress(true);
        if (myAddress != null && myAddress.toString().contains(LOCAL_IP_FIRST_PORTION)) {
            tempAddress = myAddress;
        } else {
            tempAddress = BnjUtil.getLocalIpAddress();
        }
        if (tempAddress != null) {
            String myIpAddress = tempAddress.getHostAddress();
            myIpAddress = "http://" + myIpAddress + ":" + PORT + "/" + MAIN_JSON;
            return myIpAddress;
        } else {
            return null;
        }
    }

    /**
     * This method is responsible for upload updated apk
     * in local server. for sharing app
     *
     * @return Apk file
     */
    private File backUpMainApk() {

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List pkgAppsList = mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
        String myApplicationPackageName = mContext.getPackageName();
        String myApplicationName = mContext.getResources().getString(R.string.app_name);
        for (Object object : pkgAppsList) {

            ResolveInfo resolveInfo = (ResolveInfo) object;
            File appFile = new File(resolveInfo.activityInfo.applicationInfo.publicSourceDir);

            String file_name = resolveInfo.loadLabel(mContext.getPackageManager()).toString();

            if (file_name.equalsIgnoreCase(myApplicationName) &&
                    appFile.toString().contains(myApplicationPackageName)) {
                return appFile;
            }
        }
        return null;
    }

    private File createAndUploadJsonFile() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;

            InetAddress tempAddress;

            final InetAddress myAddress = BnjUtil.getMyDeviceInetAddress(true);

            if (myAddress != null && myAddress.toString().contains(LOCAL_IP_FIRST_PORTION)) {
                tempAddress = myAddress;
            } else {
                tempAddress = BnjUtil.getLocalIpAddress();

            }

            String myIpAddress = tempAddress.getHostAddress();
            myIpAddress = "http://" + myIpAddress + ":" + PORT + "/";

            Log.e("InAppUpdateTest", "My ip address: " + myIpAddress);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(Constants.InAppUpdate.LATEST_VERSION_KEY, version);
            jsonObject.addProperty(Constants.InAppUpdate.LATEST_VERSION_CODE_KEY, "" + pInfo.versionCode);
            jsonObject.addProperty(Constants.InAppUpdate.URL_KEY, myIpAddress + MAIN_APK); // TODO change correct url
            jsonObject.addProperty(Constants.InAppUpdate.RELEASE_NOTE_KEY, "Some feature added and bug fixed");

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    mContext.getString(R.string.app_name));

            file.mkdir();

            File jsonFile = new File(file, MAIN_JSON);
            try {
                Writer output = new BufferedWriter(new FileWriter(jsonFile));
                output.write(jsonObject.toString());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                mContext.getString(R.string.app_name) + "/" + MAIN_JSON);
        return file;
    }

    private void copyFile(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
