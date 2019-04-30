/*
package com.w3engineers.unicef.telemesh.ui.inappshare;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityInAppShareBinding;
import com.w3engineers.unicef.util.helper.InAppShareUtil;
import com.w3engineers.unicef.util.helper.NetworkConfigureUtil;

*/
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *//*

public class InAppShareActivity extends BaseActivity {

    private InAppShareViewModel inAppShareViewModel;
    private ActivityInAppShareBinding activityInAppShareBinding;

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

    @Override
    protected void startUI() {

        activityInAppShareBinding = (ActivityInAppShareBinding) getViewDataBinding();

        setTitle(getString(R.string.settings_share_app));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        disableState();

        inAppShareViewModel = getViewModel();
        uiOperationServerAddress();

        inAppShareViewModel.checkInAppShareState();
    }

    private void uiOperationServerAddress() {

        inAppShareViewModel.appShareStateLiveData.observe(this, isInAppShareEnable -> {
            if (isInAppShareEnable) {

                activityInAppShareBinding.appShareProgress.setVisibility(View.GONE);
                activityInAppShareBinding.scrollView.setVisibility(View.VISIBLE);

                // Expose all server side info
                activityInAppShareBinding.shareWifiIdPass.setText(NetworkConfigureUtil.getInstance().getNetworkConfig());
                activityInAppShareBinding.textViewUrl.setText(InAppShareUtil.getInstance().serverAddress);
                activityInAppShareBinding.imageViewQrCode.setImageBitmap(InAppShareUtil.getInstance().serverAddressBitmap);

            } else {
                disableState();
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
        activityInAppShareBinding.appShareProgress.setVisibility(View.VISIBLE);
        activityInAppShareBinding.scrollView.setVisibility(View.GONE);
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
                return (T) ServiceLocator.getInstance().getInAppShareViewModel(getApplication());
            }
        }).get(InAppShareViewModel.class);
    }
}
*/
