package com.w3engineers.unicef.telemesh.ui.settings;

import android.app.AlertDialog;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.webkit.MimeTypeMap;

import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RightMeshDataSource;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.Utils;

import java.io.File;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [08-Oct-2018 at 3:14 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [08-Oct-2018 at 3:14 PM].
 * * --> <Second Editor> on [08-Oct-2018 at 3:14 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [08-Oct-2018 at 3:14 PM].
 * * --> <Second Reviewer> on [08-Oct-2018 at 3:14 PM].
 * * ============================================================================
 **/
public class SettingsViewModel extends AndroidViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog alertDialog;


    public SettingsViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean getCheckedStatus() {
        return SharedPref.getSharedPref(getApplication().getApplicationContext())
                .readBoolean(Constants.preferenceKey.IS_NOTIFICATION_ENABLED);
    }

    public void onCheckedChanged(boolean checked) {
        SharedPref.getSharedPref(getApplication().getApplicationContext())
                .write(Constants.preferenceKey.IS_NOTIFICATION_ENABLED, checked);
    }

    public String getAppLanguage() {

        String language = SharedPref.getSharedPref(App.getContext()).read(Constants.preferenceKey.APP_LANGUAGE_DISPLAY);
        return !language.equals("") ? language : App.getContext().getString(R.string.demo_language);
    }

    public void setLocale(String lang, String landDisplay) {

        SharedPref.getSharedPref(getApplication().getApplicationContext()).write(Constants.preferenceKey.APP_LANGUAGE, lang);
        SharedPref.getSharedPref(getApplication().getApplicationContext()).write(Constants.preferenceKey.APP_LANGUAGE_DISPLAY, landDisplay);

        LanguageUtil.setAppLanguage(getApplication().getApplicationContext(), lang);
    }

    /**
     * Call this method during in-app-share and
     * this api mainly work on in background thread for preparing a backup apk
     * then share in bluetooth in foreground
     *
     * @param context - for preparing a alert dialog need to send ui context
     *                so we getting this context from settings fragment
     */
    public void startInAppShare(@NonNull Context context) {
//        alertDialog = Utils.getInstance().getProgressDialog(context);

        compositeDisposable.add(backupApkAndGetPath()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> inAppShareProcess(s, context), Throwable::printStackTrace));
    }

    private Single<String> backupApkAndGetPath() {
        return Single.fromCallable(() -> Utils.getInstance().backupApkAndGetPath());
    }

    /**
     * In-App share via intent sharing
     * After sharing delete the sharing apk from backup folder
     * @param filePath - file path getting from storage backup folder
     */
    private void inAppShareProcess(String filePath, Context context) {

//        if (alertDialog != null && alertDialog.isShowing()) {
//            alertDialog.dismiss();
//        }

        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk");
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType(mimeType);

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(sharingIntent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;

            if (packageName.contains("android.bluetooth")) {
                sharingIntent.setPackage(packageName);
                sharingIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID
                        + ".provider", new File(filePath));

                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(sharingIntent);
                return;
            }
        }
    }

//     This api is unused
//    public void openWallet() {
//        RightMeshDataSource.getRmDataSource().openRmSettings();
//    }
}
