package com.w3engineers.appshare.application.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.w3engineers.appshare.R;
import com.w3engineers.appshare.util.helper.InAppShareUtil;
import com.w3engineers.appshare.util.helper.NetworkConfigureUtil;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
public class InAppShareActivity extends BaseActivity {

    private InAppShareViewModel inAppShareViewModel;

//    private ActivityInAppShareBinding activityInAppShareBinding;
//    private ActivityAppShareBinding activityInAppShareBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_in_app_share;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimary;
    }

    private ProgressBar progressBar;
    private ScrollView scrollView;
    private TextView wifiId, wifiUrl;
    private ImageView qrCode;

    @Override
    protected void startUI() {

//        activityInAppShareBinding = (ActivityAppShareBinding) getViewDataBinding();

        setTitle(getString(R.string.settings_share_app));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initUI();

        disableState();

        inAppShareViewModel = getViewModel();

        inAppShareViewModel.startInAppShareProcess();

        uiOperationServerAddress();

        inAppShareViewModel.checkInAppShareState();
    }

    private void initUI() {
        progressBar = findViewById(R.id.appShare_progress);
        scrollView = findViewById(R.id.scroll_view);
        wifiId = findViewById(R.id.share_wifi_id_pass);
        wifiUrl = findViewById(R.id.text_view_url);
        qrCode = findViewById(R.id.image_view_qr_code);
    }

    private void uiOperationServerAddress() {

        inAppShareViewModel.appShareStateLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isInAppShareEnable) {
                if (isInAppShareEnable) {

                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                    // Expose all server side info
                    wifiId.setText(NetworkConfigureUtil.getInstance().getNetworkConfig());
                    wifiUrl.setText(InAppShareUtil.getInstance().serverAddress);
                    qrCode.setImageBitmap(InAppShareUtil.getInstance().serverAddressBitmap);

                } else {
                    disableState();
                }
            }
        });
    }

    @Override
    protected void stopUI() {
        super.stopUI();
        // Stop In app share server
        inAppShareViewModel.stopServerProcess();
        inAppShareViewModel.resetAllInfo();
    }

    private void disableState() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Reset and restart RM service if RM is stopped
        inAppShareViewModel.resetRM();
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
