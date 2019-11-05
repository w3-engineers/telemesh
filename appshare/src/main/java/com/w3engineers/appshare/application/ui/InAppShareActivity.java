package com.w3engineers.appshare.application.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.w3engineers.appshare.R;
import com.w3engineers.appshare.util.helper.InAppShareUtil;
import com.w3engineers.appshare.util.helper.NetworkConfigureUtil;

import pl.droidsonroids.gif.GifImageView;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
public class InAppShareActivity extends AppCompatActivity {

    private InAppShareViewModel inAppShareViewModel;
    private Toolbar toolbar;
    private GifImageView progressBar;
    private ScrollView scrollView;
    private TextView wifiId, wifiPass, wifiUrl;
    private ImageView qrCode, wifiQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_share);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        setStatusBarColor();

        setTitle(getString(R.string.settings_share_app));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initUI();

        disableState();

        inAppShareViewModel = getViewModel();

        boolean permission = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(this);
        }

        if (!permission) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                String packageName = getPackageName();
                intent.setData(Uri.parse("package:" + packageName));
                startActivityForResult(intent, 119);

            } else {
                appShareStart();
            }
        } else {
            appShareStart();
        }
    }

    private void setStatusBarColor() {

        int statusBarColor = statusBarColor();

        if (statusBarColor > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, statusBarColor));
            }
        }
    }

    protected int statusBarColor() {
        return R.color.colorPrimary;
    }

    private void appShareStart() {
        inAppShareViewModel.startInAppShareProcess();
        uiOperationServerAddress();
        inAppShareViewModel.checkInAppShareState();
    }

    private void initUI() {
        progressBar = findViewById(R.id.appShare_progress);
        scrollView = findViewById(R.id.scroll_view);
        wifiId = findViewById(R.id.share_wifi_id);
        wifiPass = findViewById(R.id.share_wifi_id_pass);
        wifiUrl = findViewById(R.id.text_view_url);
        qrCode = findViewById(R.id.image_view_qr_code);
        wifiQrCode = findViewById(R.id.image_view_qr_code_network);
    }

    private void uiOperationServerAddress() {

        inAppShareViewModel.appShareStateLiveData.observe(this, isInAppShareEnable -> {
            if (isInAppShareEnable) {

                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                // Expose all server side info
                wifiId.setText("\"" + NetworkConfigureUtil.getInstance().getNetworkName() + "\"");
                wifiPass.setText(getPasswordText());
                wifiUrl.setText(InAppShareUtil.getInstance().serverAddress);
                qrCode.setImageBitmap(InAppShareUtil.getInstance().serverAddressBitmap);
                wifiQrCode.setImageBitmap(InAppShareUtil.getInstance().wifiAddressBitmap);

            } else {
                disableState();
            }
        });

        inAppShareViewModel.processFailedLiveData.observe(this, errorMessage -> {
            runOnUiThread(() -> {
                Toast.makeText(InAppShareActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }

    private SpannableString getPasswordText() {
        String pass = NetworkConfigureUtil.getInstance().getNetworkPass();
        String passText = String.format(getResources().getString(R.string.using_password), pass);
        SpannableString spannableString = new SpannableString(passText);

        int startIndex = passText.length() - pass.length();

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGradientPrimary)),
                startIndex, passText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 119) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
                appShareStart();
                return;
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        inAppShareViewModel.stopServerProcess();
        inAppShareViewModel.stopDirect();
        inAppShareViewModel.resetAllInfo();
        inAppShareViewModel.resetRM();
    }

    private void disableState() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private InAppShareViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new InAppShareViewModel(getApplication());
            }
        }).get(InAppShareViewModel.class);
    }
}
