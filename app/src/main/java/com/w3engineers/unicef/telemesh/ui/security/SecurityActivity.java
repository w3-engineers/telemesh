package com.w3engineers.unicef.telemesh.ui.security;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.mesh.application.data.local.wallet.WalletManager;
import com.w3engineers.mesh.util.DialogUtil;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivitySecurityBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.WalletUtil;
import com.w3engineers.unicef.util.helper.CustomDialogUtil;
import com.w3engineers.unicef.util.helper.WalletPrepareListener;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

import java.util.List;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class SecurityActivity extends BaseActivity {

    private ActivitySecurityBinding mBinding;
    private SecurityViewModel mViewModel;
    private String mUserName;
    private int mAvatarIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_security;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivitySecurityBinding) getViewDataBinding();
        mViewModel = getViewModel();

        parseIntent();
        initView();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.button_next:
                UIHelper.hideKeyboardFrom(this, mBinding.editTextBoxPassword);
                isValidPassword(mBinding.editTextBoxPassword.getText().toString());
                break;
            case R.id.button_skip:
                UIHelper.hideKeyboardFrom(this, mBinding.editTextBoxPassword);
                requestMultiplePermissions();
                break;
        }
    }

    private void initView() {
        setClickListener(mBinding.buttonNext, mBinding.buttonSkip);
        mViewModel.textChangeLiveData.observe(this, this::nextButtonControl);
        mViewModel.textEditControl(mBinding.editTextBoxPassword);

        mBinding.editTextBoxPassword.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void nextButtonControl(String nameText) {
        if (!TextUtils.isEmpty(nameText) &&
                nameText.length() >= Constants.DefaultValue.MINIMUM_PASSWORD_LIMIT) {

            mBinding.buttonNext.setBackgroundResource(R.drawable.ractangular_gradient);
            mBinding.buttonNext.setTextColor(getResources().getColor(R.color.white));
            mBinding.buttonNext.setClickable(true);
        } else {
            mBinding.buttonNext.setBackgroundResource(R.drawable.ractangular_white);
            mBinding.buttonNext.setTextColor(getResources().getColor(R.color.new_user_button_color));
            mBinding.buttonNext.setClickable(false);
        }
    }

    protected void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            CustomDialogUtil.showProgressDialog(SecurityActivity.this);

                            HandlerUtil.postBackground(() -> goNext(), 100);
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

    protected void goNext() {

        String password = mBinding.editTextBoxPassword.getText() + "";

        if (TextUtils.isEmpty(password)) {
            password = Constants.DEFAULT_PASSWORD;
        }

        String finalPassword = password;

        WalletUtil.getInstance(this).createWallet(password, new WalletPrepareListener() {
            @Override
            public void onGetWalletInformation(String address, String publickKey) {

                CustomDialogUtil.dismissProgressDialog();

                if (mViewModel.storeData(mUserName, mAvatarIndex, finalPassword, address, publickKey)) {

                    runOnUiThread(() -> {
                        CustomDialogUtil.dismissProgressDialog();

                        Intent intent = new Intent(SecurityActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        if (CreateUserActivity.sInstance != null) {
                            CreateUserActivity.sInstance.finish();
                        }
                    });

                }
            }

            @Override
            public void onWalletLoadError(String errorMessage) {
                Log.e("walletLoad", "Error: " + errorMessage);
                runOnUiThread(() -> {
                    CustomDialogUtil.dismissProgressDialog();
                    Toaster.showShort(errorMessage);
                });
            }
        });
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IntentKeys.USER_NAME)) {
            mUserName = intent.getStringExtra(Constants.IntentKeys.USER_NAME);
            mAvatarIndex = intent.getIntExtra(Constants.IntentKeys.AVATAR_INDEX, 0);
        }
    }

    public void isValidPassword(final String password) {

        if (mViewModel.isValidPassword(password)) {
            requestMultiplePermissions();
        } else {
            if (!mViewModel.isValidChar(password)) {
                Toaster.showShort("Letter missing in password");
                return;
            }

            if (!mViewModel.isDigitPassword(password)) {
                Toaster.showShort("Digit missing in password");
                return;
            }

            if (!mViewModel.isValidSpecial(password)) {
                Toaster.showShort("Special character missing");
                return;
            }
        }
    }


    private SecurityViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getSecurityViewModel(getApplication());
            }
        }).get(SecurityViewModel.class);
    }
}
