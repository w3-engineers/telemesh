package com.w3engineers.unicef.telemesh.ui.aboutus;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityAboutUsBinding;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [03-Oct-2018 at 12:24 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [03-Oct-2018 at 12:24 PM].
 * * --> <Second Editor> on [03-Oct-2018 at 12:24 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [03-Oct-2018 at 12:24 PM].
 * * --> <Second Reviewer> on [03-Oct-2018 at 12:24 PM].
 * * ============================================================================
 **/
public class AboutUsActivity extends BaseActivity {

    private ServiceLocator serviceLocator;
    private AboutUsViewModel aboutUsViewModel;
    private ActivityAboutUsBinding mBinding;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
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

        aboutUsViewModel = getViewModel();
        mBinding = (ActivityAboutUsBinding) getViewDataBinding();
        setTitle(getString(R.string.activity_about_us));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.setAboutViewModel(aboutUsViewModel);


    }

    private AboutUsViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                serviceLocator = ServiceLocator.getInstance();
                return (T) serviceLocator.getAboutUsViewModel(getApplication());
            }
        }).get(AboutUsViewModel.class);
    }
}
