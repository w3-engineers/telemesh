package com.w3engineers.unicef.telemesh.ui.searching;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivitySearchingBinding;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class SearchingActivity extends BaseActivity {

    private ActivitySearchingBinding mBinding;
    private SearchingViewModel mViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_searching;
    }


    @Override
    protected void startUI() {
        mBinding = (ActivitySearchingBinding) getViewDataBinding();
        mViewModel = getViewModel();

        initView();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.text_view_background) {
            finish();
        }
    }

    private void initView() {
        setClickListener(mBinding.textViewBackground);
    }

    private SearchingViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) ServiceLocator.getInstance().getSearchingViewModel(getApplication());
            }
        }).get(SearchingViewModel.class);
    }
}
