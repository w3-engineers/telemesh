/*
package com.w3engineers.unicef.telemesh.ui.importwallet;

import android.Manifest;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.mesh.application.data.local.wallet.WalletManager;
import com.w3engineers.mesh.util.lib.mesh.HandlerUtil;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityImportWalletBinding;
import com.w3engineers.unicef.util.WalletUtil;
import com.w3engineers.unicef.util.helper.CustomDialogUtil;
import com.w3engineers.unicef.util.helper.DexterPermissionHelper;
import com.w3engineers.unicef.util.helper.WalletPrepareListener;
import com.w3engineers.unicef.util.helper.uiutil.UIHelper;

*/
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 *//*


public class ImportWalletActivity extends BaseActivity {

    private ActivityImportWalletBinding mBinding;
    private ImportWalletViewModel mViewModel;
    private String mWalletPath;
    private Uri mWalletUri;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_wallet;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityImportWalletBinding) getViewDataBinding();
        mViewModel = getViewModel();
        initView();
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.image_view_back:
                finish();
                break;
            case R.id.button_continue:
                continueAction();
                break;
        }
    }

    private void continueAction() {
        if (mBinding.editTextPassword.getText() != null &&
                mBinding.editTextPassword.getText().length() >= 8) {
            requestMultiplePermissions();
        } else {
            Toaster.showShort(getResources().getString(R.string.enter_eight_digit_password));
        }
    }

    private void initView() {
        parseIntent();

        setClickListener(mBinding.imageViewBack, mBinding.buttonContinue);

        if (TextUtils.isEmpty(mWalletPath) && !TextUtils.isEmpty(Constants.CURRENT_ADDRESS) && !TextUtils.isEmpty(Constants.DEFAULT_ADDRESS) && Constants.CURRENT_ADDRESS.equals(Constants.DEFAULT_ADDRESS.trim())) {
            mBinding.textViewPinInstruction.setText("Your default password is:  " + Constants.DEFAULT_PASSWORD);
        }

        mBinding.editTextPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                UIHelper.hideKeyboardFrom(this, mBinding.editTextBoxPassword);
                continueAction();
                return true;
            }
            return false;
        });
    }

    private void gotoProfileCreatePage() {

        String password = mBinding.editTextPassword.getText().toString();

        if (TextUtils.isEmpty(mWalletPath)) {

            WalletManager.getInstance().loadWallet(this, password, new WalletManager.WalletLoadListener() {
                @Override
                public void onWalletLoaded(String walletAddress, String publicKey) { successWalletResponse(walletAddress, publicKey, password); }

                @Override
                public void onError(String message) { failedWalletResponse(message); }
            });
        } else {
            WalletUtil.getInstance(this).importWallet(Constants.WALLET_URI, password, new WalletPrepareListener() {
                @Override
                public void onGetWalletInformation(String address, String publickKey) { successWalletResponse(address, publickKey, password); }

                @Override
                public void onWalletLoadError(String errorMessage) { failedWalletResponse(errorMessage); }
            });

        }

    }

    public void failedWalletResponse(String message) {
        runOnUiThread(() -> {
            CustomDialogUtil.dismissProgressDialog();
            Toaster.showShort(message);
        });
    }

    public void successWalletResponse(String address, String publickKey, String password) {
        if (mViewModel.storeData(address, password, publickKey)) {

            runOnUiThread(() -> {
                CustomDialogUtil.dismissProgressDialog();

                // TODO remove wallet option
//                Intent intent = new Intent(ImportWalletActivity.this, CreateUserActivity.class);
//                intent.putExtra(Constants.IntentKeys.PASSWORD, mBinding.editTextPassword.getText());
//                startActivity(intent);
            });
        }
    }

    protected void requestMultiplePermissions() {

        DexterPermissionHelper.getInstance().requestForPermission(this, () -> {
                    CustomDialogUtil.showProgressDialog(ImportWalletActivity.this);

                    HandlerUtil.postBackground(() -> gotoProfileCreatePage(), 100);

                },
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        */
/*Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            CustomDialogUtil.showProgressDialog(ImportWalletActivity.this);

                            HandlerUtil.postBackground(() -> gotoProfileCreatePage(), 100);
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
                }).withErrorListener(error -> requestMultiplePermissions()).onSameThread().check();*//*

    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IntentKeys.WALLET_PATH)) {
            mWalletPath = intent.getStringExtra(Constants.IntentKeys.WALLET_PATH);
          //  String filename = mWalletPath.substring(mWalletPath.lastIndexOf("/") + 1);
           // mBinding.textViewFileName.setText(filename);
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
*/
