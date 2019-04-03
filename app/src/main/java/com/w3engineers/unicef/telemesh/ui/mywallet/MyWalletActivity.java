package com.w3engineers.unicef.telemesh.ui.mywallet;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.RightMeshDataSource;
import com.w3engineers.unicef.telemesh.data.provider.ServiceLocator;
import com.w3engineers.unicef.telemesh.databinding.ActivityMyWalletBinding;
import com.w3engineers.unicef.telemesh.ui.buydata.BuyDataActivity;
import com.w3engineers.unicef.telemesh.ui.selldata.SellDataActivity;
import com.w3engineers.unicef.telemesh.ui.settings.SettingsViewModel;


/**
 * * ============================================================================
 * * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [10-Jan-2019 at 10:37 AM].
 * * Email: sikderfaysal@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [10-Jan-2019 at 10:37 AM].
 * * --> <Second Editor> on [10-Jan-2019 at 10:37 AM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [10-Jan-2019 at 10:37 AM].
 * * --> <Second Reviewer> on [10-Jan-2019 at 10:37 AM].
 * * ============================================================================
 **/
public class MyWalletActivity extends BaseActivity implements View.OnClickListener{

    private MyWalletViewModel viewModel;
    private ActivityMyWalletBinding mBinding;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_wallet;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected void startUI() {

        setTitle(getString(R.string.settings_open_wallet));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = getViewModel();
        mBinding = (ActivityMyWalletBinding) getViewDataBinding();
        mBinding.buttonSell.setOnClickListener(this);
        mBinding.buttonBuy.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String  currentBalance = SharedPref.getSharedPref(this).readInt("cr_token") + " RMESH";
        mBinding.currentBalance.setText(currentBalance);
    }

    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_buy:
                startActivity(new Intent(MyWalletActivity.this, BuyDataActivity.class));
                 break;
            case R.id.button_sell:
                startActivity(new Intent(MyWalletActivity.this, SellDataActivity.class));

                break;
        }
        super.onClick(view);
    }

    private MyWalletViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                 return (T) ServiceLocator.getInstance().getMyWalletViewModel(getApplication());
            }
        }).get(MyWalletViewModel.class);
    }
}
