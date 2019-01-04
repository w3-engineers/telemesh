package com.w3engineers.unicef.telemesh.ui.test;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.local.usertable.UserEntity;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;

import java.util.List;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [14-Sep-2018 at 4:39 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [14-Sep-2018 at 4:39 PM].
 * * --> <Second Editor> on [14-Sep-2018 at 4:39 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [14-Sep-2018 at 4:39 PM].
 * * --> <Second Reviewer> on [14-Sep-2018 at 4:39 PM].
 * * ============================================================================
 **/
public class TestActivity extends BaseActivity {

    private TestViewModel userMeshViewModel;
    private ServiceLocator serviceLocator;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void startUI() {
        userMeshViewModel = getViewModel();
        getUserResponse();
    }

    private void getUserResponse() {

        userMeshViewModel.getUserLiveData().observe(this, userEntities -> {
            if (userEntities == null || userEntities.size() == 0)
                return;

            for (UserEntity userEntity : userEntities) {
                Log.v("MIMO_SAHA::", "User name: " + userEntity.getUserFirstName()
                        + " Status: " + userEntity.isOnline() + " ID: " + userEntity.getMeshId());
            }
        });
    }

    private TestViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                serviceLocator = ServiceLocator.getInstance();
                return (T) serviceLocator.getTestViewModel();
            }
        }).get(TestViewModel.class);
    }
}
