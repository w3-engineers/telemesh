package com.w3engineers.unicef.telemesh.ui.settings;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.w3engineers.ext.strom.App;
import com.w3engineers.ext.strom.application.ui.base.BaseRxAndroidViewModel;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.util.helper.InAppShareUtil;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.NetworkConfigureUtil;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
public class SettingsViewModel extends BaseRxAndroidViewModel implements NetworkConfigureUtil.NetworkCallback {


    public SettingsViewModel(@NonNull Application application) {
        super(application);
    }

    public String SSID_Name = null;
    public String wifiInfo = null;

    public MutableLiveData<Bitmap> bitmapMutableLiveData = new MutableLiveData<>();


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

    public void startInAppShareServer() {
        this.SSID_Name = null;

        getCompositeDisposable().add(Single.fromCallable(this::getRouterConfigure)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    private Boolean getRouterConfigure() {
        return NetworkConfigureUtil.getInstance()
                .setNetworkCallback(SettingsViewModel.this).startRouterConfigureProcess();
    }

    @Override
    public void networkName(String SSID) {

        Context context = getApplication().getApplicationContext();

        this.SSID_Name = SSID;

        String wifiName = SSID_Name;
        String wifiPass = NetworkConfigureUtil.getInstance().SSID_Key;

        wifiInfo = String.format(context.getString(R.string.hotspot_id_pass), wifiName, wifiPass);
        String QrText = "WIFI:T:WPA;P:\"" + wifiPass + "\";S:" + wifiName + ";";

        qrGenerator(context, QrText);

        initServerProcess();
    }

    private void initServerProcess() {
        getCompositeDisposable().add(serverInitSingleCallable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(address -> InAppShareUtil.getInstance().qrGenerator(address),
                        Throwable::printStackTrace));
    }

    private Single<String> serverInitSingleCallable() {
        return Single.fromCallable(this::serverInit);
    }

    @Nullable
    private String serverInit() {
        return InAppShareUtil.getInstance().serverInit();
    }

    private void qrGenerator(Context context, String Value) {
        try {


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
            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, 150, 0, 0, bitMatrixWidth, bitMatrixHeight);

            bitmapMutableLiveData.postValue(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
