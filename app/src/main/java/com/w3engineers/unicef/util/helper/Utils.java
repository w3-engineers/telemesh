package com.w3engineers.unicef.util.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.w3engineers.ext.strom.App;
import com.w3engineers.unicef.telemesh.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created by: Mimo Saha on [28-Feb-2019 at 3:45 PM].
 * Email:
 * Project: telemesh.
 * Code Responsibility: <Purpose of code>
 * Edited by :
 * --> <First Editor> on [28-Feb-2019 at 3:45 PM].
 * --> <Second Editor> on [28-Feb-2019 at 3:45 PM].
 * Reviewed by :
 * --> <First Reviewer> on [28-Feb-2019 at 3:45 PM].
 * --> <Second Reviewer> on [28-Feb-2019 at 3:45 PM].
 * ============================================================================
 **/
public class Utils {

    private static Utils utils = new Utils();
    private String appFilePath = null;

    public static Utils getInstance() {
        return utils;
    }

    public void deleteBackUpApk() {
        if (!TextUtils.isEmpty(appFilePath)) {
            File file = new File(appFilePath);
            file.delete();
        }
    }

    public String backupApkAndGetPath() {

        Context context = App.getContext();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        String myApplicationPackageName = context.getPackageName();
        String myApplicationName = context.getResources().getString(R.string.app_name);

        for (Object object : pkgAppsList) {

            ResolveInfo resolveInfo = (ResolveInfo) object;
            File appFile = new File(resolveInfo.activityInfo.applicationInfo.publicSourceDir);

            try {

                String file_name = resolveInfo.loadLabel(context.getPackageManager()).toString();

                if (file_name.equalsIgnoreCase(myApplicationName) &&
                        appFile.toString().contains(myApplicationPackageName)) {

                    File file = new File(Environment.getExternalStorageDirectory().toString());
                    file.mkdirs();
                    file = new File(file.getPath() + "/" + file_name + ".apk");
                    file.createNewFile();

                    InputStream in = new FileInputStream(appFile);
                    OutputStream out = new FileOutputStream(file);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    return file.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public AlertDialog getProgressDialog(Context context) {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(context);
        tvText.setText(context.getResources().getString(R.string.processing));
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }

        return alertDialog;
    }

}
