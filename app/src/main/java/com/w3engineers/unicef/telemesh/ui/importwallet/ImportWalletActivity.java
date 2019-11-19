package com.w3engineers.unicef.telemesh.ui.importwallet;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.mesh.util.DialogUtil;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityImportWalletBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.importprofile.ImportProfileActivity;
import com.w3engineers.unicef.telemesh.ui.security.SecurityActivity;
import com.w3engineers.unicef.util.WalletUtil;
import com.w3engineers.unicef.util.helper.WalletPrepareListener;

import java.util.List;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class ImportWalletActivity extends BaseActivity {

    private ActivityImportWalletBinding mBinding;
    private ImportWalletViewModel mViewModel;
    private String mWalletPath;
    private Uri mWalletUri;

    private ProgressDialog progressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_wallet;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityImportWalletBinding) getViewDataBinding();
        mViewModel = getViewModel();
        initView();

        progressDialog = new ProgressDialog(ImportWalletActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.button_continue:
                if (mBinding.editTextPassword.getText() != null &&
                        mBinding.editTextPassword.getText().length() >= 8) {
                    requestMultiplePermissions();
                } else {
                    Toaster.showShort(getResources().getString(R.string.enter_eight_digit_password));
                }
                break;
        }
    }

    private void initView() {
        parseIntent();

        setClickListener(mBinding.imageViewBack, mBinding.buttonContinue);
    }

    private void gotoProfileCreatePage() {
        HandlerUtil.postForeground(() -> {
            progressDialog.show();
            Log.d("progress", "Progress show call");
        }, 10);

        String password = mBinding.editTextPassword.getText().toString();

        WalletUtil.getInstance(this).importWallet(Constants.WALLET_URI, password, new WalletPrepareListener() {
            @Override
            public void onGetWalletInformation(String address, String publickKey) {
                if (mViewModel.storeData(address, password, publickKey)) {

                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        Intent intent = new Intent(ImportWalletActivity.this, CreateUserActivity.class);
                        intent.putExtra(Constants.IntentKeys.PASSWORD, mBinding.editTextPassword.getText());
                        startActivity(intent);
                    });
                }
            }

            @Override
            public void onWalletLoadError(String errorMessage) {
                Log.e("walletLoad", "Error: " + errorMessage);
                runOnUiThread(() -> {
                    DialogUtil.dismissLoadingProgress();
                    Toaster.showShort(errorMessage);
                });
            }
        });
    }

    protected void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            gotoProfileCreatePage();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestMultiplePermissions();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> requestMultiplePermissions()).onSameThread().check();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IntentKeys.WALLET_PATH)) {
            mWalletPath = intent.getStringExtra(Constants.IntentKeys.WALLET_PATH);
            String filename = mWalletPath.substring(mWalletPath.lastIndexOf("/") + 1);
            mBinding.textViewFileName.setText(filename);
        }
    }

    private ImportWalletViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getImportWalletViewModel(getApplication());
            }
        }).get(ImportWalletViewModel.class);
    }
}
