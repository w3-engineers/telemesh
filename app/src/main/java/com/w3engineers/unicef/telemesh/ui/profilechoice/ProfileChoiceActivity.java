/*
package com.w3engineers.unicef.telemesh.ui.profilechoice;

import android.Manifest;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityProfileChoiceBinding;
import com.w3engineers.unicef.telemesh.ui.importprofile.ImportProfileActivity;
import com.w3engineers.unicef.util.helper.CommonUtil;

import java.util.List;

*/
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 *//*


public class ProfileChoiceActivity extends BaseActivity {

    private ActivityProfileChoiceBinding mBinding;
    private ProfileChoiceViewModel mViewModel;
    private boolean isCreateProfileCall;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_choice;
    }

    @Override
    protected void startUI() {
        mBinding = (ActivityProfileChoiceBinding) getViewDataBinding();
        mViewModel = getViewModel();

        initView();

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.button_create_account) {
            isCreateProfileCall = true;
            requestMultiplePermissions();
        } else if (view.getId() == R.id.button_import_account) {
            isCreateProfileCall = false;
            requestMultiplePermissions();
        }
    }

    private void initView() {
        setClickListener(mBinding.buttonCreateAccount, mBinding.buttonImportAccount);
    }

    private ProfileChoiceViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getProfileChoiceViewModel(getApplication());
            }
        }).get(ProfileChoiceViewModel.class);
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
                            if (isCreateProfileCall) {
                                // TODO remove wallet option
//                                startActivity(new Intent(ProfileChoiceActivity.this, CreateUserActivity.class));
                            } else {
                                startActivity(new Intent(ProfileChoiceActivity.this, ImportProfileActivity.class));
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            CommonUtil.showPermissionPopUp(ProfileChoiceActivity.this);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> requestMultiplePermissions()).onSameThread().check();
    }

}
*/
