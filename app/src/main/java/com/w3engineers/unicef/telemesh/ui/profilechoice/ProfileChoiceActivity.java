package com.w3engineers.unicef.telemesh.ui.profilechoice;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityProfileChoiceBinding;
import com.w3engineers.unicef.telemesh.ui.createuser.CreateUserActivity;
import com.w3engineers.unicef.telemesh.ui.importprofile.ImportProfileActivity;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class ProfileChoiceActivity extends BaseActivity {

    private ActivityProfileChoiceBinding mBinding;
    private ProfileChoiceViewModel mViewModel;

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
            startActivity(new Intent(ProfileChoiceActivity.this, CreateUserActivity.class));
        } else if (view.getId() == R.id.button_import_account) {
            startActivity(new Intent(ProfileChoiceActivity.this, ImportProfileActivity.class));
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
}
