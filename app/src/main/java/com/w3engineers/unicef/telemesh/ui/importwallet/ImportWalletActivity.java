package com.w3engineers.unicef.telemesh.ui.importwallet;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.databinding.ActivityImportWalletBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.importprofile.ImportProfileActivity;

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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_wallet;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityImportWalletBinding) getViewDataBinding();

        initView();
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
                    gotoProfileCreatePage();
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
        Intent intent = new Intent(ImportWalletActivity.this, CreateUserActivity.class);
        intent.putExtra(Constants.IntentKeys.PASSWORD, mBinding.editTextPassword.getText());
        intent.putExtra(Constants.IntentKeys.WALLET_PATH, mWalletPath);
        startActivity(intent);
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IntentKeys.WALLET_PATH)) {
            mWalletPath = intent.getStringExtra(Constants.IntentKeys.WALLET_PATH);
            String filename = mWalletPath.substring(mWalletPath.lastIndexOf("/") + 1);
            mBinding.textViewFileName.setText(filename);
        }
    }
}
