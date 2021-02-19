package com.w3engineers.unicef.telemesh.ui.security;

import android.Manifest;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivitySecurityBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.main.MainActivity;
import com.w3engineers.unicef.util.WalletUtil;
import com.w3engineers.unicef.util.helper.CommonUtil;
import com.w3engineers.unicef.util.helper.CustomDialogUtil;
import com.w3engineers.unicef.util.helper.WalletAddressHelper;
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
    private boolean isDefaultPassword;

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
//                requestMultiplePermissions();
                isValidPassword(mBinding.editTextBoxPassword.getText().toString(), false);
                break;
            case R.id.button_skip:
                UIHelper.hideKeyboardFrom(this, mBinding.editTextBoxPassword);
                isValidPassword(null, true);
                break;
            case R.id.text_view_show_password:
                updatePasswordVisibility();
                break;
        }
    }

    private void initView() {
        setClickListener(mBinding.buttonNext, mBinding.buttonSkip, mBinding.textViewShowPassword);
        mViewModel.textChangeLiveData.observe(this, this::nextButtonControl);
        mViewModel.textEditControl(mBinding.editTextBoxPassword);

        mBinding.editTextBoxPassword.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        mBinding.editTextBoxPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                UIHelper.hideKeyboardFrom(this, mBinding.editTextBoxPassword);
                isValidPassword(mBinding.editTextBoxPassword.getText().toString(), false);
                return true;
            }
            return false;
        });
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

    protected void requestMultiplePermissions(boolean isSkip) {

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

                            HandlerUtil.postBackground(() -> goNext(isSkip), 100);
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            CommonUtil.showPermissionPopUp(SecurityActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> requestMultiplePermissions(isSkip)).onSameThread().check();
    }

//    public String myAddress = "0x550de922bec427fc1b279944e47451a89a4f7cag";
//    public String friendAddress = "0x3b52d4e229fd5396f468522e68f17cfe471b2e03";
//    public String publicKey = "0x04647ba47589ace7e9636029e5355b9b71c1c66ccd3c1b7c127f3c21016dacea7d3aa12e41eca790d4c3eff8398fd523dc793c815da7bbdbf29c8744b761ad8e4c";


    protected void goNext(boolean isSkip) {

        String password = mBinding.editTextBoxPassword.getText() + "";

        if (TextUtils.isEmpty(password) || isSkip) {
            password = Constants.DEFAULT_PASSWORD;
            isDefaultPassword = true;
        }

        String finalPassword = password;

//        processCompleted(myAddress, publicKey, finalPassword);

        WalletUtil.getInstance(this).createWallet(password, new WalletPrepareListener() {
            @Override
            public void onGetWalletInformation(String address, String publickKey) {
                processCompleted(address, publickKey, finalPassword);
            }

            @Override
            public void onWalletLoadError(String errorMessage) {
                runOnUiThread(() -> {
                    CustomDialogUtil.dismissProgressDialog();
                    Toaster.showShort(errorMessage);
                });
            }
        });
    }

    public void processCompleted(String address, String publickKey, String finalPassword) {

        CustomDialogUtil.dismissProgressDialog();

        if (mViewModel.storeData(mUserName, mAvatarIndex, finalPassword, address, publickKey)) {

            runOnUiThread(() -> {
                if (isDefaultPassword) {
                    WalletAddressHelper.writeDefaultAddress(address, SecurityActivity.this);
                }

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

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IntentKeys.USER_NAME)) {
            mUserName = intent.getStringExtra(Constants.IntentKeys.USER_NAME);
            mAvatarIndex = intent.getIntExtra(Constants.IntentKeys.AVATAR_INDEX, -1);
        }
    }

    public void isValidPassword(final String password, boolean isSkip) {

        if (mViewModel.isValidPassword(password) || isSkip) {
            requestMultiplePermissions(isSkip);
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

    private void updatePasswordVisibility() {
        String currentText = mBinding.textViewShowPassword.getText().toString();
        if (currentText.equals(getResources().getString(R.string.show_password))) {
            mBinding.textViewShowPassword.setText(getResources().getString(R.string.hide_password));
            mBinding.editTextBoxPassword.setPasswordShow(true);
        } else {
            mBinding.textViewShowPassword.setText(getResources().getString(R.string.show_password));
            mBinding.editTextBoxPassword.setPasswordShow(false);
        }
    }
}
